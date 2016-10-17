package myServer2;

import java.util.ArrayList;
import java.io.File;

import javax.net.ssl.SSLSocket;

public class MainServer {

	private static int port;
	private static int coordination;
	private static MainServer instance;
	private static ArrayList<String> username;
	private static ArrayList<String> password;
	private static ArrayList<Boolean> locked;
	private static ArrayList<SSLSocket> server_connection;
	private static ArrayList<Conf> server_state;

	public static synchronized MainServer getInstance() {
		if(instance == null) {
			instance = new MainServer();
		}
		return instance;
	}

	public static void main(String[] args){
		AuthenLoader loader = new AuthenLoader();
		username = loader.loadUserNameList("authen.txt");
		password = loader.loadPasswordList("authen.txt");
		for(int i=0;i<username.size();i++){
			locked.add(false);
		}
		port = 80;
		coordination = 4444;

		new MainServerServerConnection().start();
		new MainServerClientConnection().start();
	}

	public static void RemoveServerAnnouncement(SSLSocket serverSocket){

	}

	public static void AddServer(SSLSocket serverSocket,Conf server_conf){
		server_connection.add(serverSocket);
		server_state.add(server_conf);
	}

	public int GetPort(){
		return port;
	}

	public int GetCoordination(){
		return coordination;
	}

	public ArrayList<String> GetUserName(){
		return username;
	}

	public ArrayList<String> GetPassword() {
		return password;
	}

	public void AddServerConnection(SSLSocket new_connection){
		server_connection.add(new_connection);
	}


	public void AddServerState(Conf new_Conf){
		server_state.add(new_Conf);
	}

	public synchronized boolean CheckIDLockedOrLockIt(int index){
		boolean check = locked.get(index);
		locked.set(index, false);
		return check;
	}
}
