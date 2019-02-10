package com.bakdata.streams_store;

import java.util.List;

public class ProcessorMetadata {
	
	private String host;
    private int port;
    private List<Integer> topicPartitions;
    
    public ProcessorMetadata(String host, int port, List<Integer> topicPartitions) {
    	this.host = host;
    	this.port = port;
    	this.topicPartitions = topicPartitions;
    }
    
}
