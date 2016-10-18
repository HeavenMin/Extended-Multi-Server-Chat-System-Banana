package myServer3;


import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.net.ssl.SSLSocket;
import org.json.simple.JSONObject;

public class MainServer {

	private static int port;
	private static int coordination;
	private static MainServer instance;
	private static ArrayList<String> username;
	private static ArrayList<String> password;
	private static ArrayList<Boolean> locked;
	private static ArrayList<SSLSocket> server_connection;
	private static ArrayList<Conf> server_state;
	private static ArrayList<String> room_id;
	private static ArrayList<String> room_belong_server;

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
		locked = new ArrayList<Boolean>();
		server_connection = new ArrayList<SSLSocket>();
		server_state = new ArrayList<Conf>();
		room_id = new ArrayList<String>();
		room_belong_server = new ArrayList<String>();

		for(int i=0;i<username.size();i++){
			locked.add(true);
		}

		port = 80;
		coordination = 90;

		//开始运行分别接收client和server连接的thread

		new MainServerServerConnection().start();
		new MainServerClientConnection().start();

	}



	public synchronized void RemoveServerAnnouncement(SSLSocket serverSocket,Conf serverConf){
		server_connection.remove(serverSocket);
		server_state.remove(serverConf);
		ArrayList<Integer> should_remove = new ArrayList<Integer>();
		for(int i = room_belong_server.size() -1 ; i>=0 ; i--){
			if(room_belong_server.get(i).equals(serverConf.getServerid())){
				should_remove.add(i);
			}
		}
		for(int i=0;i<should_remove.size();i++){
			room_id.remove(i);
			room_belong_server.remove(i);
		}
		for(int i=0;i<server_connection.size();i++){
			try{
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(server_connection.get(i).getOutputStream(), "UTF-8"));
				JSONObject notifyremainserver = ServerMessage.notifyOneServerDown(serverConf);
				writer.write(notifyremainserver.toJSONString());
				writer.write("\n");
				writer.flush();

			}
			catch(Exception e){
			}
		}
	}

	public synchronized void AddServer(SSLSocket serverSocket,Conf server_conf){
		server_connection.add(serverSocket);
		server_state.add(server_conf);
	}

	public synchronized int GetPort(){
		return port;
	}

	public synchronized int GetCoordination(){
		return coordination;
	}

	public synchronized ArrayList<String> GetUserName(){
		return username;
	}

	public synchronized ArrayList<String> GetPassword() {
		return password;
	}

	public synchronized void AddServerConnection(SSLSocket new_connection){
		server_connection.add(new_connection);
	}

	public synchronized ArrayList<SSLSocket> GetServerConnection(){
		return server_connection;
	}

	public synchronized void AddServerState(Conf new_Conf){
		server_state.add(new_Conf);
	}

	public synchronized ArrayList<Conf> GetServerState(){
		return server_state;
	}

	public synchronized boolean CheckIDLockedOrLockIt(int index){
		boolean check = locked.get(index);
		locked.set(index, false);
		return check;
	}

	public synchronized void AddRoom(String room_name){
		room_id.add(room_name);
	}

	public synchronized void AddRoomBelongingServer(String server_name){
		room_belong_server.add(server_name);
	}

	public synchronized void DeleteRoom(String room_name){
		int index = -1;
		for(int i=0;i<room_id.size();i++){
			if(room_id.get(i).equals(room_name)){
				index = i;
			}
		}

		room_id.remove(index);
		room_belong_server.remove(index);

	}

	public synchronized ArrayList<String> GetRooms(){
		return room_id;
	}

	public synchronized ArrayList<String> GetRoomBelongServer(){
		return room_belong_server;
	}
}