package ChatGUI;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageReceiveThread implements Runnable {

	private SSLSocket socket;
	private State state;
	private boolean debug;

	private BufferedReader in;

	private JSONParser parser = new JSONParser();

	private boolean run = true;
	
	private MessageSendThread messageSendThread;
	
	private ChatGUI gui;

	public MessageReceiveThread(SSLSocket socket, State state, MessageSendThread messageSendThread, boolean debug,ChatGUI gui) throws IOException {
		this.gui = gui;
		this.socket = socket;
		this.state = state;
		this.messageSendThread = messageSendThread;
		this.debug = debug;
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
	//	System.setProperty("javax.net.debug","all");
	}

	@Override
	public void run() {

		try {
			this.in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			JSONObject message;
			while (run) {
				message = (JSONObject) parser.parse(in.readLine());
				if (debug) {
					gui.updateOutputTextWithNewLine("Receiving: " + message.toJSONString());
					gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
				}
				MessageReceive(socket, message);
			}
			System.exit(0);
			in.close();
			socket.close();
		} catch (ParseException e) {
			gui.updateOutputTextWithNewLine("Message Error: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			gui.updateOutputTextWithNewLine("Communication Error: " + e.getMessage());
			System.exit(1);
		}

	}

	public void MessageReceive(SSLSocket socket, JSONObject message)
			throws IOException, ParseException {
		String type = (String) message.get("type");
		
		// server reply of #newidentity
		if (type.equals("newidentity")) {
			boolean approved = Boolean.parseBoolean((String) message.get("approved"));
			
			// terminate program if failed
			if (!approved) {
				gui.updateOutputTextWithNewLine(state.getIdentity() + " already in use.");
				socket.close();
				System.exit(1);
			}
			return;
		}
		
		// server reply of #list
		if (type.equals("roomlist")) {
			JSONArray array = (JSONArray) message.get("rooms");
			// print all the rooms
			gui.updateOutputTextWithoutNewLine("List of chat rooms:");
			for (int i = 0; i < array.size(); i++) {
				gui.updateOutputTextWithoutNewLine(" " + array.get(i));
			}
			gui.updateOutputTextJustNewLine();
			gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			return;
		}

		// server sends roomchange
		if (type.equals("roomchange")) {

			// identify whether the user has quit!
			if (message.get("roomid").equals("")) {
				// quit initiated by the current client
				if (message.get("identity").equals(state.getIdentity())) {
					gui.updateOutputTextWithNewLine(message.get("identity") + " has quit!");
					in.close();
					System.exit(1);
				} else {
					gui.updateOutputTextWithoutNewLine(message.get("identity") + " has quit!");
					gui.updateOutputTextJustNewLine();
					gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
				}
			// identify whether the client is new or not
			} else if (message.get("former").equals("")) {
				// change state if it's the current client
				if (message.get("identity").equals(state.getIdentity())) {
					state.setRoomId((String) message.get("roomid"));
				}
				gui.updateOutputTextWithNewLine(message.get("identity") + " moves to "
						+ (String) message.get("roomid"));
				gui.updateOutputTextWithNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			// identify whether roomchange actually happens
			} else if (message.get("former").equals(message.get("roomid"))) {
				gui.updateOutputTextWithoutNewLine("room unchanged");
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			// print the normal roomchange message
			else {
				// change state if it's the current client
				if (message.get("identity").equals(state.getIdentity())) {
					state.setRoomId((String) message.get("roomid"));
				}
				
				gui.updateOutputTextWithoutNewLine(message.get("identity") + " moves from " + message.get("former") + " to "
						+ message.get("roomid"));
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			return;
		}
		
		// server reply of #who
		if (type.equals("roomcontents")) {
			JSONArray array = (JSONArray) message.get("identities");
			gui.updateOutputTextWithoutNewLine(message.get("roomid") + " contains");
			for (int i = 0; i < array.size(); i++) {
				gui.updateOutputTextWithoutNewLine(" " + array.get(i));
				if (message.get("owner").equals(array.get(i))) {
					gui.updateOutputTextWithoutNewLine("*");
				}
			}
			gui.updateOutputTextJustNewLine();
			gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			return;
		}
		
		// server forwards message
		if (type.equals("message")) {
			gui.updateOutputTextWithoutNewLine(message.get("identity") + ": "
					+ message.get("content"));
			gui.updateOutputTextWithNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			return;
		}
		
		
		// server reply of #createroom
		if (type.equals("createroom")) {
			boolean approved = Boolean.parseBoolean((String)message.get("approved"));
			String temp_room = (String)message.get("roomid");
			if (!approved) {
				gui.updateOutputTextWithoutNewLine("Create room " + temp_room + " failed.");
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			else {
				gui.updateOutputTextWithoutNewLine("Room " + temp_room + " is created.");
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			return;
		}
		
		// server reply of # deleteroom
		if (type.equals("deleteroom")) {
			boolean approved = Boolean.parseBoolean((String)message.get("approved"));
			String temp_room = (String)message.get("roomid");
			if (!approved) {
				gui.updateOutputTextWithoutNewLine("Delete room " + temp_room + " failed.");
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			else {
				gui.updateOutputTextWithoutNewLine("Room " + temp_room + " is deleted.");
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			return;
		}
		
		// server directs the client to another server
		if (type.equals("route")) {
			String temp_room = (String)message.get("roomid");
			String host = (String)message.get("host");
			int port = Integer.parseInt((String)message.get("port"));
			
			// connect to the new server
			if (debug) {
				gui.updateOutputTextWithNewLine("Connecting to server " + host + ":" + port);
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket temp_socket = (SSLSocket) sslsocketfactory.createSocket(host,port);
			
			// send #movejoin
			DataOutputStream out = new DataOutputStream(temp_socket.getOutputStream());
			JSONObject request = ClientMessages.getMoveJoinRequest(state.getIdentity(), state.getRoomId(), temp_room);
			if (debug) {
				gui.updateOutputTextWithoutNewLine("Sending: " + request.toJSONString());
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			send(out, request);
			
			// wait to receive serverchange
			BufferedReader temp_in = new BufferedReader(new InputStreamReader(temp_socket.getInputStream()));
			JSONObject obj = (JSONObject) parser.parse(temp_in.readLine());
			
			if (debug) {
				gui.updateOutputTextWithoutNewLine("Receiving: " + obj.toJSONString());
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			
			// serverchange received and switch server
			if (obj.get("type").equals("serverchange") && obj.get("approved").equals("true")) {
				messageSendThread.switchServer(temp_socket, out);
				switchServer(temp_socket, temp_in);
				String serverid = (String)obj.get("serverid");
				gui.updateOutputTextWithoutNewLine(state.getIdentity() + " switches to server " + serverid);
				gui.updateOutputTextJustNewLine();
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			// receive invalid message
			else {
				temp_in.close();
				out.close();
				temp_socket.close();
				gui.updateOutputTextWithNewLine("Server change failed");
				gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
			}
			return;
		}
		
		if (debug) {
			gui.updateOutputTextWithNewLine("Unknown Message: " + message);
			gui.updateOutputTextWithoutNewLine("[" + state.getRoomId() + "] " + state.getIdentity() + "> ");
		}
	}
	
	public void switchServer(SSLSocket temp_socket, BufferedReader temp_in) throws IOException {
		in.close();
		in = temp_in;
		socket.close();
		socket = temp_socket;
	}

	private void send(DataOutputStream out, JSONObject obj) throws IOException {
		out.write((obj.toJSONString() + "\n").getBytes("UTF-8"));
		out.flush();
	}
}