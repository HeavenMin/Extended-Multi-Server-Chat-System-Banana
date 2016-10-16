package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
	
	BlockingQueue<ClientMessage> messageQueue;
	private static MessageQueue instance;
	
	private MessageQueue() {
		messageQueue = new LinkedBlockingQueue<>();
	}
	
	public static MessageQueue getInstance() {
		if (instance == null) {
			instance = new MessageQueue();
		}
		return instance;
	}
	
	public void add(ClientMessage msg) {
		messageQueue.add(msg);
	}
	
	public ClientMessage take() throws InterruptedException {
		return messageQueue.take();
	}

}
