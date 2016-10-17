package myServer2;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

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
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(MainServer.getInstance().GetCoordination());
			while(true){
				SSLSocket new_server_socket = (SSLSocket)listeningClientSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new_server_socket.getInputStream(), "UTF-8"));
				String msg = reader.readLine();
				JSONObject msgJsonObj = (JSONObject) parser.parse(msg);
				String serverid = (String) msgJsonObj.get("serverAddress");
				String serverAddress = (String) msgJsonObj.get("serverAddress");
				int clientsPort = Integer.parseInt((String) msgJsonObj.get("clientsPort"));
				int coordinationPort = Integer.parseInt((String) msgJsonObj.get("coordinationPort"));
				Conf serverConf = new Conf(serverid, serverAddress, clientsPort, coordinationPort);
				MainServer.getInstance().AddServerConnection(new_server_socket);
				MainServer.getInstance().AddServerState(serverConf);
				new HeartBeatTest(new_server_socket,serverConf).start();
			}
		}
		catch(Exception e){

		}
	}
}
