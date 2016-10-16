package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.util.ArrayList;

public class ClientState {
	private static ClientState instance;
	private ArrayList<Client> clientList;
	private ArrayList<String> lockClientList;
	
	private ClientState() {
		clientList = new ArrayList<>();
		lockClientList = new ArrayList<>();
	}
	
	public synchronized static ClientState getInstance() {
		if(instance == null) {
			instance = new ClientState();
		}
		return instance;
	}
	
	public synchronized Client getClient(String clientid) {
		for (Client client : clientList) {
			if (client.getClientid().equals(clientid)) {
				return client;
			}
		}
		return null;
	}
	
	public synchronized ArrayList<Client> getClientList() {
		return clientList;
	}
	
	public synchronized boolean isClientidExist(String clientid) {
		if (lockClientList.contains(clientid)) {
			return true;
		}
		if (getClient(clientid) != null) {
			return true;
		}
		return false;
	}
	
	public synchronized void addClient(Client client) {
		clientList.add(client);
	}
	
	public synchronized void removeClient(Client client) {
		clientList.remove(client);
	}
	
	public synchronized void addLockClient(String clientid) {
		lockClientList.add(clientid);
	}
	
	public synchronized void releaseLockClient(String clientid) {
		lockClientList.remove(clientid);
	}
	
	public synchronized ArrayList<String> getAllClientList() {
		ArrayList<Client> allClient = new ArrayList<>();
		allClient.addAll(clientList);
		ArrayList<String> allClientid = new ArrayList<>();
		for (Client client : allClient) {
			allClientid.add(client.getClientid());
		}
		return allClientid;
	}
	

}
