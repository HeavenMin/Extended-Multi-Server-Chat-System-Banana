package myServer3;

/*
 * Name : Min Gao

 * COMP90015 Distributed Systems 2016 SM2
 * Project1-Multi-Server Chat System
 * Login Name : ming1
 * Student Number : 773090
 */

//è¿™ä»½æ›´æ”¹äº†serversocket ä¸ºsslserversocketï¼Œå…¶ä»–æ›´æ”¹ä¹Ÿéƒ½æ˜¯æ ¼å¼�åŽŸå› 

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
		//æ–°åŠ å…¥ä¸ºäº†ä½¿sslèƒ½å¤Ÿä½¿ç”¨è€Œæ›´æ”¹çš„è®¾ç½®,å��å­—å†�è®®
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");

		try{

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
