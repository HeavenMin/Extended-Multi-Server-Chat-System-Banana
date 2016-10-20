package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

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
