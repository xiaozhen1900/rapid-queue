package com.google.code.rapid.queue.server.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageProperties;

public class MessageConvertUtil {

	public static List<Message> toThriftMessageList(List<com.google.code.rapid.queue.model.Message> sourceList) {
		if(sourceList == null) return Collections.EMPTY_LIST;
		
		List<Message> resultList = new ArrayList<Message>(sourceList.size());
		for(com.google.code.rapid.queue.model.Message source : sourceList) {
			Message target = toThriftMessage(source);
			resultList.add(target);
		}
		return resultList;
	}

	public static List<com.google.code.rapid.queue.model.Message> totoMessageBrokerMessageList(List<Message> sourceList) {
		if(sourceList == null) return Collections.EMPTY_LIST;
		
		List<com.google.code.rapid.queue.model.Message> resultList = new ArrayList<com.google.code.rapid.queue.model.Message>(sourceList.size());
		for(Message source : sourceList) {
			com.google.code.rapid.queue.model.Message target = toMessageBrokerMessage(source);
			resultList.add(target);
		}
		return resultList;
	}
	
	public static Message toThriftMessage(com.google.code.rapid.queue.model.Message source) {
		if(source == null) return new Message();
		
		Message result = new Message();
		result.setBody(source.getBody());
		result.setExchange(source.getExchange());
		result.setRouterKey(source.getRouterKey());
		result.setMessageProperties(toThriftMessageProperties(source.getMessageProperties()));
		return result;
	}
	
	public static com.google.code.rapid.queue.model.Message toMessageBrokerMessage(Message source) {
		if(source == null) return null;
		
		com.google.code.rapid.queue.model.Message result = new com.google.code.rapid.queue.model.Message();
		result.setBody(source.getBody());
		result.setExchange(source.getExchange());
		result.setRouterKey(source.getRouterKey());
		result.setMessageProperties(toMessageBrokerMessageProperties(source.getMessageProperties()));
		return result;
	}
	
	public static MessageProperties toThriftMessageProperties(com.google.code.rapid.queue.model.MessageProperties source) {
		if(source == null) return null;
		
		MessageProperties result = new MessageProperties();
		result.setContentEncoding(source.getContentEncoding());
		result.setContentLength(source.getContentLength());
		result.setContentType(source.getContentType());
		result.setDeliveryMode(source.getDeliveryMode());
		result.setExpiration(source.getExpiration());
		result.setMessageId(source.getMessageId());
		result.setPriority(source.getPriority());
		result.setSourceApp(source.getSourceApp());
		result.setSourceIp(source.getSourceIp());
		result.setSourceUser(source.getSourceUser());
		return result;
	}
	
	public static com.google.code.rapid.queue.model.MessageProperties toMessageBrokerMessageProperties(MessageProperties source) {
		if(source == null) return null;
		
		com.google.code.rapid.queue.model.MessageProperties result = new com.google.code.rapid.queue.model.MessageProperties();
		result.setContentEncoding(source.getContentEncoding());
		result.setContentLength(source.getContentLength());
		result.setContentType(source.getContentType());
		result.setDeliveryMode(source.getDeliveryMode());
		result.setExpiration(source.getExpiration());
		result.setMessageId(source.getMessageId());
		result.setPriority(source.getPriority());
		result.setSourceApp(source.getSourceApp());
		result.setSourceIp(source.getSourceIp());
		result.setSourceUser(source.getSourceUser());
		return result;
	}
}