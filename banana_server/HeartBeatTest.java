package myServer3;



import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.net.SocketTimeoutException;



import javax.net.ssl.SSLSocket;



import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;

//这个thread只建立心跳连接并未送任何信息过去，所以不用在server处理json里面进行处理

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
			MainServer.getInstance().RemoveServerAnnouncement(sslSocket,serverConf);
		}
		catch (Exception e) {
			MainServer.getInstance().RemoveServerAnnouncement(sslSocket,serverConf);
		}
	}
}