package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.util.ArrayList;

public class ServerState {
	
	private static ServerState instance;
	private Conf thisServer;
	private ArrayList<Conf> activeServers;
	
	private ServerState() {
		activeServers = new ArrayList<>();
	}
	
	public static synchronized ServerState getInstance() {
		if(instance == null) {
			instance = new ServerState();
		}
		return instance;
	}
	
	public synchronized void serverConnected(Conf server) {
		activeServers.add(server);
	}
	
	public ArrayList<Conf> getServerList() {
		return activeServers;
	}
	
	public synchronized Conf getRemoteServerConf(String serverid) {
		for (Conf serverConf : activeServers) {
			if (serverConf.getServerid().equals(serverid)) {
				return serverConf;
			}
		}
		return null;
	}
	
	public synchronized void setThisServer(Conf serverConf) {
		this.thisServer = serverConf;
	}
	
	public synchronized Conf getThisServer() {
		return thisServer;
	}

}
