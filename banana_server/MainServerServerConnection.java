package myServer2;

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
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		System.setProperty("javax.net.debug","all");

		try{
			//开启接收server连接的thread
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(MainServer.getInstance().GetCoordination());
			while(true){
				SSLSocket new_server_socket = (SSLSocket)listeningClientSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new_server_socket.getInputStream(), "UTF-8"));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new_server_socket.getOutputStream(), "UTF-8"));
				String msg = reader.readLine();
				JSONObject msgJsonObj = (JSONObject) parser.parse(msg);
				//读取送过来的有关想要连接的server的server的信息
				String serverid = (String) msgJsonObj.get("serverid");
				String serverAddress = (String) msgJsonObj.get("serverAddress");
				int clientsPort = Integer.parseInt((String) msgJsonObj.get("clientsPort"));
				int coordinationPort = Integer.parseInt((String) msgJsonObj.get("coordinationPort"));
				//将信息整合到一个conf类里
				Conf serverConf = new Conf(serverid, serverAddress, clientsPort, coordinationPort);

				//开始向各个服务器发送各种信息，包括1.对想要连接main server的服务器告知所有其他已存在服务器的信息,包括房间信息
				JSONObject newServerReply = ServerMessage.newServerReply(MainServer.getInstance().GetServerState(),MainServer.getInstance().GetRooms(),MainServer.getInstance().GetRoomBelongServer());
				writer.write(newServerReply.toJSONString());
				writer.newLine();
				writer.flush();

				//2.对所有已存在的old server发送一条增加一个新server的信息
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

				//把新server的信息加入到已连接server列表中，同时在对应位置保存conf的信息
				MainServer.getInstance().AddServerConnection(new_server_socket);
				MainServer.getInstance().AddServerState(serverConf);
				//添加一个mainhall
				MainServer.getInstance().AddRoom("MainHall-"+serverid);
				MainServer.getInstance().AddRoomBelongingServer(serverid);

				//运用sslsocket和conf的信息（包括位置），开始一个心跳检测
				sleep(10000);
				new HeartBeatTest(new_server_socket,serverConf,reader).start();
			}
		}
		catch(Exception e){

		}
	}
}
