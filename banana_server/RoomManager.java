package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.util.ArrayList;

public class RoomManager {
	
	private static RoomManager instance;
	private ArrayList<Room> localRoomList;
	private ArrayList<String> lockRoomidList;
	private ArrayList<Room> otherRoomList;
	
	private RoomManager() {
		this.localRoomList = new ArrayList<>();
		this.lockRoomidList = new ArrayList<>();
		this.otherRoomList = new ArrayList<>();
	}
	
	public synchronized static RoomManager getInstance() {
		if(instance == null) {
			instance = new RoomManager();
		}
		return instance;
	}
	
	public Room getLocalRoom(String roomid) {
		for (Room room : localRoomList) {
			if (room.getRoomid().equals(roomid)) {
				return room;
			}
		}
		return null;
	}
	
	public Room getRemoteRoom(String roomid) {
		for (Room room : otherRoomList) {
			if (room.getRoomid().equals(roomid)) {
				return room;
			}
		}
		return null;
	}
	
	//also can use synchronized(lockRoomidList)
	public synchronized void lockRoom(String roomid) {
		lockRoomidList.add(roomid);
	}
	
	public synchronized void releaseLockRoom(String roomid) {
		lockRoomidList.remove(roomid);
	}
	
	public synchronized void addOtherServerRoom(String roomid, String serverid) {
		otherRoomList.add(new Room(roomid,serverid));
	}
	
	public synchronized void removeOtherServerRoom(String roomid) {
		Room room = getRemoteRoom(roomid);
		otherRoomList.remove(room);
	}
	
	public synchronized void joinRoom(String clientid, String preRoomid, String roomid) {
		if (!preRoomid.equals("")) {
			Room preRoom = getLocalRoom(preRoomid);
			preRoom.removeClient(clientid);
		}
		Room room = getLocalRoom(roomid);
		room.addClient(clientid);
	}
	
	public synchronized void quitRoom(String clientid, String roomid) {
		Room room = getLocalRoom(roomid);
		room.removeClient(clientid);
	}
	
	public synchronized void createRoom(String roomid, String roomOwner, String roomServerid) {
		Room room = new Room(roomid, roomOwner, roomServerid);
		localRoomList.add(room);
	}
	
	public synchronized void deleteRoom(String roomid) {
		Room room = getLocalRoom(roomid);
		localRoomList.remove(room);
	}
	
	//localRoomList and lockRoomidList [s]
	public synchronized boolean isRoomExist(String roomid) {
		if (lockRoomidList.contains(roomid)) {
			return true;
		}
		if (getLocalRoom(roomid) != null) {
			return true;
		}
		return false;
	}
	
	public synchronized boolean isLocalRoomExist(String roomid) {
		if (getLocalRoom(roomid) != null) {
			return true;
		}
		return false;
	}
	
	public synchronized boolean isRemoteRoomExist(String roomid) {
		if (getRemoteRoom(roomid) != null) {
			return true;
		}
		return false;
	}
	
	public synchronized ArrayList<String> getAllRoom() {
		ArrayList<Room> allRoom = new ArrayList<>();
		allRoom.addAll(localRoomList);
		allRoom.addAll(otherRoomList);
		ArrayList<String> allRoomid = new ArrayList<>();
		for (Room room : allRoom) {
			allRoomid.add(room.getRoomid());
		}
		return allRoomid;
	}

}
