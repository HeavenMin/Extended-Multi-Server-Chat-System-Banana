package myServer3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainServerInfo {
	
	private SSLSocket mainServerSocket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private String mainServerAddress;
	private int mainServerPort;
	
	public MainServerInfo(String mainServerAddress, int mainServerPort) {
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		this.mainServerAddress = mainServerAddress;
		this.mainServerPort = mainServerPort;
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			mainServerSocket = (SSLSocket) sslsocketfactory.createSocket(
										this.mainServerAddress, this.mainServerPort);
			reader = new BufferedReader(
					new InputStreamReader(this.mainServerSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(
					new OutputStreamWriter(this.mainServerSocket.getOutputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public SSLSocket getMainServerSocket() {
		return mainServerSocket;
	}
	
	public synchronized String read() {
		String reply;
		try {
			reply = reader.readLine();
			System.out.println("debug");
			System.out.println(reply);
			return reply;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void write(String msg) {
		try {
			writer.write(msg);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

}
