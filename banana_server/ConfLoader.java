package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ConfLoader {
	
	public Conf loadConf(String serverid, String confPath) {
		Conf conf = null;
		String[] serverConf = readServerConf(confPath);
		for (int i = 0; i < serverConf.length; i++) {
			if (serverid.equals(serverConf[i].split("\t")[0])) {
				String serverAddress = serverConf[i].split("\t")[1];
				int clientsPort = Integer.parseInt(serverConf[i].split("\t")[2]);
				int coordinationPort = Integer.parseInt(serverConf[i].split("\t")[3]);
				conf = new Conf(serverid, serverAddress, clientsPort, coordinationPort);
			}
		}
		return conf;
	}
	
	//initialize other server state
	public void initializeServerState(String serverid, String confPath) {
		String[] serverConf = readServerConf(confPath);
		for (int i = 0; i < serverConf.length; i++) {
			if(!serverid.equals(serverConf[i].split("\t")[0])) {
				ServerState.getInstance().serverConnected(loadConf((serverConf[i].split("\t")[0]), confPath));
				RoomManager.getInstance().addOtherServerRoom(
						"MainHall-" + serverConf[i].split("\t")[0] , serverConf[i].split("\t")[0]);
			}
		}
	}
	
	private String[] readServerConf(String confPath) {
		String[] serverConf = new String[totalServerNum(confPath)];
		int i = 0;
		try {
			Scanner inputServerConf = new Scanner(new FileInputStream(confPath));
			while (inputServerConf.hasNextLine()) {
				serverConf[i] = inputServerConf.nextLine();
				i++;
			}
			inputServerConf.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return serverConf;
	}
	
	private int totalServerNum(String confPath) {
		int i= 0;
		try {
			Scanner inputServerConf = new Scanner(new FileInputStream(confPath));
			while (inputServerConf.hasNextLine()) {
				inputServerConf.nextLine();
				i++;
			}
			inputServerConf.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return i;
	}

}
