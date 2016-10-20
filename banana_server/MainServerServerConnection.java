package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MainServerServerConnection extends Thread{

	private JSONParser parser = new JSONParser();

	@Override

	public void run(){
		
		System.out.println("The mainServer is set up!");
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");

		try{

			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(MainServer.getInstance().GetCoordination());
			while(true){
				SSLSocket new_server_socket = (SSLSocket)listeningClientSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new_server_socket.getInputStream(), "UTF-8"));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new_server_socket.getOutputStream(), "UTF-8"));
				String msg = reader.readLine();
				JSONObject msgJsonObj = (JSONObject) parser.parse(msg);
				String serverid = (String) msgJsonObj.get("serverid");
				String serverAddress = (String) msgJsonObj.get("serverAddress");
				int clientsPort = Integer.parseInt((String) msgJsonObj.get("clientsPort"));
				int coordinationPort = Integer.parseInt((String) msgJsonObj.get("coordinationPort"));

				Conf serverConf = new Conf(serverid, serverAddress, clientsPort, coordinationPort);

				JSONObject newServerReply = ServerMessage.newServerReply(MainServer.getInstance().GetServerState(),MainServer.getInstance().GetRooms(),MainServer.getInstance().GetRoomBelongServer());
				writer.write(newServerReply.toJSONString());
				writer.newLine();
				writer.flush();

				JSONObject notifyOldSerevr = ServerMessage.noticeNewServerComing(serverConf);
				ArrayList<Conf> old_server_state = MainServer.getInstance().GetServerState();
				for(int i=0;i<old_server_state.size();i++){
					String server_address = old_server_state.get(i).getServerAddress().getHostAddress();
					int server_coordinate_port = old_server_state.get(i).getCoordinationPort();
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(server_address,server_coordinate_port);
					BufferedWriter old_server_writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
					old_server_writer.write(notifyOldSerevr.toJSONString());
					old_server_writer.newLine();
					old_server_writer.flush();

				}
				
				MainServer.getInstance().AddServerConnection(new_server_socket);
				MainServer.getInstance().AddServerState(serverConf);
				MainServer.getInstance().AddRoom("MainHall-"+serverid);
				MainServer.getInstance().AddRoomBelongingServer(serverid);

				new HeartBeatTest(new_server_socket,serverConf,reader).start();
			}
		}
		catch(Exception e){

		}

	}

}