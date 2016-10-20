package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import java.io.BufferedReader;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HeartBeatTest extends Thread{

	SSLSocket sslSocket;
	Conf serverConf;
	JSONParser parser = new JSONParser();
	BufferedReader reader;
	
	public HeartBeatTest(SSLSocket sslSocket,Conf serverConf,BufferedReader reader){
		this.sslSocket = sslSocket;
		this.serverConf = serverConf;
		this.reader = reader;
	}

	@Override
	public void run() {
		try{
			sslSocket.setSoTimeout(20000);
			while(true){
				String msg = reader.readLine();
				JSONObject msgJsonObj = (JSONObject) parser.parse(msg);
				String type = (String) msgJsonObj.get("type");
				System.out.println(type);
				if("addroom".equals(type)){
					String room_id = (String) msgJsonObj.get("roomID");
					String server_id = (String) msgJsonObj.get("serverID");
					MainServer.getInstance().AddRoom(room_id);
					MainServer.getInstance().AddRoomBelongingServer(server_id);
				}
				if("deleteroom".equals(type)){
					String room_id = (String) msgJsonObj.get("roomID");
					MainServer.getInstance().DeleteRoom(room_id);
				}
			}
		}
		catch (SocketTimeoutException e){
			System.out.println("one server cannot pass heart beat test");
			MainServer.getInstance().RemoveServerAnnouncement(sslSocket,serverConf);
		}
		catch (Exception e) {
			System.out.println("one server force exist");
			MainServer.getInstance().RemoveServerAnnouncement(sslSocket,serverConf);
		}
	}
}