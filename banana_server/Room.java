package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.util.ArrayList;

public class Room {
	private String roomid;
	private ArrayList<String> clientsListInRoom;
	private String roomOwner;
	private String roomServerid;
	
	public Room(String roomid, String roomOwner, String roomServerid) {
		this.roomid = roomid;
		this.clientsListInRoom = new ArrayList<>();
		this.roomOwner = roomOwner;
		this.roomServerid = roomServerid;
	}
	
	public Room(String roomid, String roomServerid) {
		this.roomid = roomid;
		this.clientsListInRoom = new ArrayList<>();
		this.roomOwner = "";
		this.roomServerid = roomServerid;
	}
	
	public String getRoomid() {
		return roomid;
	}
	
	public String getRoomOwner() {
		return roomOwner;
	}
	
	public String getRoomServerid() {
		return roomServerid;
	}
	
	public synchronized ArrayList<String> getClientsListInRoom() {
		return clientsListInRoom;
	}
	
	public synchronized void addClient(String clientid) {
		clientsListInRoom.add(clientid);
	}
	
	public synchronized void removeClient(String clientid) {
		clientsListInRoom.remove(clientid);
	}

}
