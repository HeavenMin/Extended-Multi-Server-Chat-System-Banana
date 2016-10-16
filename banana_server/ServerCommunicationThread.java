package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerCommunicationThread extends Thread {
	
	private int coordinationPort;
	private ServerSocket listeningServerSocket;
	volatile private boolean isRunning = true;
	
	public ServerCommunicationThread(int coordinationPort) {
		try{
			this.coordinationPort = coordinationPort;
			this.listeningServerSocket = new ServerSocket(this.coordinationPort);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try{
			while(isRunning) {
				Socket serverSocket = listeningServerSocket.accept();
				new ServerMsgDealerThread(serverSocket).start();
			}
			listeningServerSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
