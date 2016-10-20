package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
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
