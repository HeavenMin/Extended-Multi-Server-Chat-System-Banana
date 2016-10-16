package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
	private String clientid;
	private String room;
	private String ownedRoom;
	private String serverid;
	private Socket clientSocket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private boolean isQuitRequestSend;
	
	public Client(String clientid, String room, String serverid, Socket clientSocket) {
		this.clientid = clientid;
		this.room = room;
		this.ownedRoom = null;
		this.serverid = serverid;
		this.clientSocket = clientSocket;
		isQuitRequestSend = false;
		try {
			reader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(
					clientSocket.getOutputStream(), "UTF-8"));	
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Client(String clientid, String room, String ownedRoom, String serverid, Socket clientSocket) {
		this.clientid = clientid;
		this.room = room;
		this.ownedRoom = ownedRoom;
		this.serverid = serverid;
		this.clientSocket = clientSocket;
		isQuitRequestSend = false;
		try {
			reader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(
					clientSocket.getOutputStream(), "UTF-8"));	
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getClientid() {
		return clientid;
	}

	public String getRoom() {
		return room;
	}

	public String getOwnedRoom() {
		return ownedRoom;
	}

	public String getServerid() {
		return serverid;
	}
	
	public Socket getClientSocket() {
		return clientSocket;
	}

	public synchronized String read() throws IOException {
		if (reader.ready()) {
//		if (reader.readLine() != null) {
			return reader.readLine();
		}
		return null;
	}
	
	public void write(String msg) {
		try {
			writer.write(msg);
			writer.newLine();
			writer.flush();
		}
		catch (IOException e) {
			ClientMessage quitrequest = new ClientMessage(ServerMessage.quit().toJSONString(), clientid);
			if (!isQuitRequestSend) {
				System.out.println("A client is disconnected abnormally! Clientid: " + clientid);
				MessageQueue.getInstance().add(quitrequest);
				isQuitRequestSend = true;
			}
			//e.printStackTrace();
		}
	}
	
	public synchronized void createRoom(String roomid) {
		this.room = roomid;
		this.ownedRoom = roomid;
	}
	
	public synchronized void changeRoom(String roomid) {
		this.room = roomid;
	}
	
	public synchronized void deleteRoom() {
		this.ownedRoom = null;
	}

}
