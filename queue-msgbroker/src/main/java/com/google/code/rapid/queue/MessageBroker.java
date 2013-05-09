package com.google.code.rapid.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.google.code.rapid.queue.exchange.TopicExchange;
import com.google.code.rapid.queue.metastore.model.Exchange;

public class MessageBroker {
	// Map<exchangeName,TopicExchange>
	private Map<String,TopicExchange> exchangeMap = null;
	
	// Map<queueName,TopicExchange>
	private Map<String,TopicQueue> queueMap = null;
	
	private MessageBrokerManager manager = new MessageBrokerManager();
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void send(Message msg) {
		if(StringUtils.isEmpty(msg.getExchange())) throw new IllegalArgumentException("'exchange' must be not empty");
		if(StringUtils.isEmpty(msg.getRouterKey())) throw new IllegalArgumentException("'routerKey' must be not empty");
		if(msg.getBody() == null) throw new IllegalArgumentException("'body' must be not null");
		
		TopicExchange exchange = lookupExchange(msg.getExchange());
		exchange.offer(msg);
	}
	
	/**
	 * 批量发送消息
	 * @param msg
	 */	
	public void sendBatch(List<Message> msgList) {
		for(Message msg : msgList) {
			send(msg);
		}
	}
	
	/**
	 * 接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 */	
	public Message receive(String queueName,int timeout) {
		TopicQueue queue = lookupQueue(queueName);
		try {
			byte[] body = queue.getQueue().poll(timeout,TimeUnit.MILLISECONDS);
			return new Message(body);
		} catch (InterruptedException e) {
			return null;
		}
	}

	/**
	 * 批量接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 * @param batchSize 批量接收的大小
	 */		
	public List<Message> receiveBatch(String queueName,int timeout,int batchSize) {
		TopicQueue queue = lookupQueue(queueName);
		List<Message> result = new ArrayList<Message>(batchSize);
		
		long totalCostTime = 0;
		long nextWaittime = timeout;
		for(int i = 0; i < batchSize; i++) {
			long start = System.currentTimeMillis();
			
			try {
				byte[] body = queue.getQueue().poll(nextWaittime,TimeUnit.MILLISECONDS);
				result.add(new Message(body));
			} catch (InterruptedException e) {
				break;
			}
			
			totalCostTime += System.currentTimeMillis() - start;
			nextWaittime = timeout - totalCostTime;
			
			if(totalCostTime >= timeout) {
				break;
			}
		}
		
		return result;
	}
	
	public MessageBrokerManager getManager() {
		return manager;
	}

	private TopicExchange lookupExchange(String exchangeName) {
		TopicExchange exchange = exchangeMap.get(exchangeName);
		if(exchange == null) {
			throw new IllegalArgumentException("not found exchange by name:"+exchangeName);
		}
		return exchange;
	}

	private TopicQueue lookupQueue(String queueName) {
		TopicQueue queue = queueMap.get(queueName);
		if(queue == null) {
			throw new IllegalArgumentException("not found queue by name:"+queueName);
		}
		return queue;
	}
	
	public class MessageBrokerManager {
	
		public void queueAdd(TopicQueue queue) {
			if(queueMap.containsKey(queue.getQueueName())) throw new IllegalArgumentException("already contain queue:"+queue.getQueueName());
			
			queueMap.put(queue.getQueueName(),queue);
		}
		
		public void queueDelete(String queueName) {
			queueUnbindAllExchange(queueName);
			TopicQueue queue = queueMap.remove(queueName);
			queue.truncate();
		}
	
		public void queueUnbindAllExchange(String queueName) {
			for(String exchangeName : exchangeMap.keySet()) {
				TopicExchange exchange = lookupExchange(exchangeName);
				exchange.unbindQueue(queueName);
			}
		}
		
		public void queueBind(String exchangeName,String queueName,String routerKey) {
			TopicExchange exchange = lookupExchange(exchangeName);
			TopicQueue queue = lookupQueue(queueName);
			queue.getRouterKeyList().add(routerKey);
			exchange.bindQueue(queue);
		}
	
		public void queueUnbind(String exchangeName,String queueName,String routerKey) {
			TopicExchange exchange = lookupExchange(exchangeName);
			exchange.unbindQueue(queueName,routerKey);
		}
		
		public void exchangeAdd(TopicExchange exchange) {
			if(exchangeMap.containsKey(exchange.getExchangeName())) throw new IllegalArgumentException("already contain exchange:"+exchange.getExchangeName());
			
			exchangeMap.put(exchange.getExchangeName(),exchange);
		}
		
		public void exchangeDelete(String exchangeName) {
			exchangeMap.remove(exchangeName);
		}
	}
}