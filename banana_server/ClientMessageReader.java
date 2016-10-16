package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.io.IOException;
import java.util.ArrayList;

public class ClientMessageReader extends Thread  {
	
	volatile boolean isRunning = true;
	
	@Override
	public void run() {
		while (isRunning) {
			ArrayList<String> clientList = ClientState.getInstance().getAllClientList();
			for (String clientid : clientList) {
				try {
					String msg = null;
					msg = ClientState.getInstance().getClient(clientid).read();
					if ( msg != null) {
						System.out.println(clientid + " send a message: " + msg);
						MessageQueue.getInstance().add(new ClientMessage(msg, clientid));
					}
					
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					//System.out.println("clientlist error!");
					continue;
				}
				
			}
		}
		
	}

}
