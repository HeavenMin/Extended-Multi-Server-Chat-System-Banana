package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
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
	
	public synchronized void serverDisconnected(Conf server) {
		activeServers.remove(server);
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
