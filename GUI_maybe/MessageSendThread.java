package ChatGUI;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */


import java.io.DataOutputStream;
import java.io.IOException;

import javax.net.ssl.SSLSocket;

import org.json.simple.JSONObject;

public class MessageSendThread implements Runnable {
	
	private SSLSocket socket;
	private DataOutputStream out;
	private State state;
	private boolean debug;
	
	// reading from console
	private ChatGUI gui;

	public MessageSendThread(SSLSocket socket, State state, boolean debug,ChatGUI gui) throws IOException {
		this.gui = gui;
		gui.addMessageSendThread(this);
		this.socket = socket;
		this.state = state;
		out = new DataOutputStream(socket.getOutputStream());
		this.debug = debug;
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
	//	System.setProperty("javax.net.debug","all");
	}

	@Override
	public void run() {
		
		try {			
			// send the #newidentity command
			MessageSend(socket, "#newidentity " + state.getIdentity());
			
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		while (true) {
			
		}
		
	}

	private void send(JSONObject obj) throws IOException {
		if (debug) {
			gui.updateOutputTextWithNewLine("Sending: " + obj.toJSONString());
			gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
		}
		out.write((obj.toJSONString() + "\n").getBytes("UTF-8"));
		out.flush();
	}
	
	// send command and check validity
	public void MessageSend(SSLSocket socket, String msg) throws IOException {
		JSONObject sendToServer = new JSONObject();
		String []array = msg.split(" ");
		if(!array[0].startsWith("#")) {
			sendToServer = ClientMessages.getMessage(msg);
			send(sendToServer);
		}
		else if(array.length == 1) {
			if(array[0].startsWith("#list")) {
				sendToServer = ClientMessages.getListRequest();
				send(sendToServer);
			}
			else if(array[0].startsWith("#quit")) {
				sendToServer = ClientMessages.getQuitRequest();
				send(sendToServer);
			}
			else if(array[0].startsWith("#who")) {
				sendToServer = ClientMessages.getWhoRequest();
				send(sendToServer);
			}
			else {
				gui.updateOutputTextWithoutNewLine("Invalid command!");
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
		}
		else if (array.length == 2) {
			if(array[0].startsWith("#joinroom")) {
				sendToServer = ClientMessages.getJoinRoomRequest(array[1]);
				send(sendToServer);
			}
			else if(array[0].startsWith("#createroom")) {
				sendToServer = ClientMessages.getCreateRoomRequest(array[1]);
				send(sendToServer);
			}
			else if(array[0].startsWith("#deleteroom")) {
				sendToServer = ClientMessages.getDeleteRoomRequest(array[1]);
				send(sendToServer);
			}
			else if (array[0].startsWith("#newidentity")) {
				sendToServer = ClientMessages.getNewIdentityRequest(array[1]);
				send(sendToServer);
			}
			else {
				gui.updateOutputTextWithNewLine("Invalid command!");
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
		}
		else {
			gui.updateOutputTextWithNewLine("Invalid command!");
			gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
		}
		
	}

	public void switchServer(SSLSocket temp_socket, DataOutputStream temp_out) throws IOException {
		// switch server initiated by the receiving thread
		// need to use synchronize
		synchronized(out) {
			out.close();
			out = temp_out;
		}
		socket = temp_socket;
	}
	
	public void sendFromGUI(String msg) {
		try {
			gui.updateOutputTextWithoutNewLine(msg);
			gui.updateOutputTextJustNewLine();
			if(!msg.startsWith("#")){
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			MessageSend(socket, msg);
		} catch (IOException e) {
			gui.updateOutputTextWithNewLine("Communication Error: " + e.getMessage());
			System.exit(1);
		}
	}
}