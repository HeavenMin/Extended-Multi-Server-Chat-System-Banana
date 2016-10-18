package myServer2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MainServerClientConnection extends Thread{

	private JSONParser parser = new JSONParser();

	@Override
	public void run(){
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		System.setProperty("javax.net.debug","all");

		try{
			//开启接收client的sslserversocket
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(MainServer.getInstance().GetPort());
			//把已经定型好的username和password弄到这个程序里
			ArrayList<String> all_user = MainServer.getInstance().GetUserName();
			ArrayList<String> all_password = MainServer.getInstance().GetPassword();
			while(true){
				SSLSocket new_server_socket = (SSLSocket)listeningClientSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new_server_socket.getInputStream(), "UTF-8"));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new_server_socket.getOutputStream(), "UTF-8"));
				String msg = reader.readLine();
				JSONObject msgJsonObj = (JSONObject) parser.parse(msg);
				String user_name = (String) msgJsonObj.get("username");
				String password = (String) msgJsonObj.get("password");
				boolean exist = false;
				//如果用户存在，用户名密码对应正确，用户名并没有被锁住那么返回true让用户顺利登陆
				int index = all_user.indexOf(user_name);
				if(index != -1 && all_password.get(index).equals(password) && MainServer.getInstance().CheckIDLockedOrLockIt(index)){
					exist = true;
				}
				if(!exist){
				}
				//返回成功与否给用户
				JSONObject reply = ServerMessage.userLogin(exist,MainServer.getInstance().GetServerState());
				writer.write(reply.toJSONString());
				writer.newLine();
				writer.flush();
			}
		}
		catch(Exception e){

		}
	}
}
