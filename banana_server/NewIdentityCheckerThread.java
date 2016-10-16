package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NewIdentityCheckerThread extends Thread {
	private int clientport;
	private String serverid;
    volatile private boolean isRunning = true;
	private JSONParser parser = new JSONParser();
	
	public NewIdentityCheckerThread(int clientport, String serverid) {
		this.clientport = clientport;
		this.serverid = serverid;
	}
	
	@Override
	public void run() {
		ServerSocket listeningClientSocket = null;
		Socket clientSocket = null;
		try {
			listeningClientSocket = new ServerSocket(clientport);
			while(isRunning) {
				clientSocket = listeningClientSocket.accept();
				//System.out.println("A new client is connected!");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"));
				String msg = reader.readLine();
				JSONObject msgJsonObj = (JSONObject) parser.parse(msg);
				String msgType = (String) msgJsonObj.get("type");
				String clientid = (String) msgJsonObj.get("identity");
				
				if (msgType.equals("newidentity")) {
					//System.out.println(ClientState.getInstance().isClientidExist(clientid));
					if (ClientState.getInstance().isClientidExist(clientid) ||
							!IdentityChecker.isIdentityValid(clientid)) {
						System.out.println("The identity a new client request was alreadly used or unvalid!");
						writer.write(ServerMessage.newIdentityReplyToClient(false).toJSONString());
						writer.newLine();
						writer.flush();
						continue;
						
					}
					ArrayList<Conf> otherServerList = ServerState.getInstance().getServerList();
					String request = ServerMessage.lockIdentityRequest(serverid, clientid).toJSONString();
					boolean vote = true;
					for (Conf serverConf : otherServerList) {
						Socket socket = new Socket(serverConf.getServerAddress(),serverConf.getCoordinationPort());
						BufferedReader serverReader = new BufferedReader(
								new InputStreamReader(socket.getInputStream(), "UTF-8"));
						BufferedWriter serverWriter = new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
						serverWriter.write(request);
						serverWriter.newLine();
						serverWriter.flush();
						String reply = serverReader.readLine();
						JSONObject replyJsonObj = (JSONObject) parser.parse(reply);
						vote = vote && (((String) replyJsonObj.get("locked")).equals("true"));
						socket.close();
					}
					
					writer.write(ServerMessage.newIdentityReplyToClient(vote).toJSONString());
					writer.newLine();
					writer.flush();
					
					if (vote) {
						String room = "MainHall-" + serverid;
						Client newClient = new Client(clientid, room, serverid, clientSocket);
						ClientState.getInstance().addClient(newClient);
						RoomManager.getInstance().joinRoom(clientid, "", room);
						System.out.println("A new Client created. ClientID: " + clientid);
						writer.write(ServerMessage.roomChange(clientid, "", room).toJSONString());
						writer.newLine();
						writer.flush();
						
						ArrayList<String> clientList = ClientState.getInstance().getAllClientList();
						for (String clientName : clientList) {
							if (!clientName.equals(clientid)) {
								if(ClientState.getInstance().getClient(clientName).getRoom().equals(
										room)) {
									ClientState.getInstance().getClient(clientName).write(
											ServerMessage.roomChange(clientid, "", room).toJSONString());
								}
							}
						}
					}
					else {
						writer.close();
						clientSocket.close();
					}
					
					for (Conf serverConf : otherServerList) {
						Socket socket = new Socket(serverConf.getServerAddress(),serverConf.getCoordinationPort());
						BufferedWriter serverWriter = new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
						request = ServerMessage.releaseIdentityLock(serverid, clientid).toJSONString();
						serverWriter.write(request);
						serverWriter.newLine();
						serverWriter.flush();
						serverWriter.close();
						socket.close();
					}
						
				}
				if (msgType.equals("movejoin")) {	//need to consider a person regist same id there
					String mainRoom = "MainHall-" + serverid;
					String preRoom = (String) msgJsonObj.get("former");
					String toRoom = (String) msgJsonObj.get("roomid");
					if (ClientState.getInstance().isClientidExist(clientid)) {
						System.out.println("The identity was registered by another user!");
						writer.write(ServerMessage.serverChange(serverid, false).toJSONString());
						writer.newLine();
						writer.flush();
						continue;
						
					}
					if (!RoomManager.getInstance().isLocalRoomExist(toRoom)) {
						Client newClient = new Client(clientid, mainRoom, serverid, clientSocket);
						ClientState.getInstance().addClient(newClient);
						RoomManager.getInstance().joinRoom(clientid, "", mainRoom);
						writer.write(ServerMessage.serverChange(serverid, true).toJSONString());
						writer.newLine();
						writer.flush();
						
						ArrayList<String> clientList = ClientState.getInstance().getAllClientList();
						for (String clientName : clientList) {
							if(ClientState.getInstance().getClient(clientName).getRoom().equals(
									mainRoom)) {
								ClientState.getInstance().getClient(clientName).write(
										ServerMessage.roomChange(clientid, preRoom, mainRoom).toJSONString());
							}
						}
					}
					else if (RoomManager.getInstance().isLocalRoomExist(toRoom)) {
						Client newClient = new Client(clientid, toRoom, serverid, clientSocket);
						ClientState.getInstance().addClient(newClient);
						RoomManager.getInstance().joinRoom(clientid, "", toRoom);
						writer.write(ServerMessage.serverChange(serverid, true).toJSONString());
						writer.newLine();
						writer.flush();
						
						ArrayList<String> clientList = ClientState.getInstance().getAllClientList();
						for (String clientName : clientList) {
							if(ClientState.getInstance().getClient(clientName).getRoom().equals(
									toRoom)) {
								ClientState.getInstance().getClient(clientName).write(
										ServerMessage.roomChange(clientid, preRoom, toRoom).toJSONString());
							}
							/*
							if (!clientName.equals(clientid)) {
								if(ClientState.getInstance().getClient(clientName).getRoom().equals(
										toRoom)) {
									ClientState.getInstance().getClient(clientName).write(
											ServerMessage.roomChange(clientid, preRoom, toRoom).toJSONString());
								}
							}
							*/
						}
					}
					else {
						writer.close();
						clientSocket.close();
					}
				}
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		finally {
			if(listeningClientSocket != null) {
				try {
					listeningClientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
