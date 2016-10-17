package myServer2;

/*
 * Name : Min Gao

 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

//这份更改了serversocket 为sslserversocket，其他更改也都是格式原因

import java.io.IOException;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ServerCommunicationThread extends Thread {
	
	private int coordinationPort;
	private SSLServerSocket listeningServerSocket;
	volatile private boolean isRunning = true;
	
	public ServerCommunicationThread(int coordinationPort) {
		try{
			this.coordinationPort = coordinationPort;
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			this.listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(this.coordinationPort);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		//新加入为了使ssl能够使用而更改的设置,名字再议
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		System.setProperty("javax.net.debug","all");
				
		try{
			//这里是用来进行心跳检测的代码部分
			ArrayList<Conf> remoteServer = ServerState.getInstance().getServerList();
			for (Conf serverConf : remoteServer) {
				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(serverConf.getServerAddress(), serverConf.getCoordinationPort());
				new HeartBeatTest(sslsocket,serverConf).start();
			}
			//以下是大哥原来的代码，上面是我新加的
			while(isRunning) {
				SSLSocket serverSocket = (SSLSocket) listeningServerSocket.accept();
				new ServerMsgDealerThread(serverSocket).start();
			}
			listeningServerSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
