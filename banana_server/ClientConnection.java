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
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ClientConnection extends Thread {
	
	volatile private boolean isRunning = true;
	private JSONParser parser = new JSONParser();
	
	@Override
	public void run() {
		while (isRunning) {
			try {
				ClientMessage msg = MessageQueue.getInstance().take();
				switch ((String) msg.getJsonMessage().get("type")) {
				case "list":
					getRoomList(msg);
					break;
				case "who":
					getRoomClient(msg);
					break;
				case "createroom":
					createRoom(msg);
					break;
				case "join":
					joinRoom(msg);
					break;
				case "deleteroom":
					deleteRoom(msg);
					break;
				case "quit":
					quitRoom(msg);
					break;
				case "message":
					broadCastMsgInRoom(msg);
					break;
				default:
					break;
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getRoomList(ClientMessage msg) {
		String msgToClient = ServerMessage.roomListReply(RoomManager.getInstance().getAllRoom()).toJSONString();
		System.out.println(msgToClient + "send to " + msg.getIdentity());
		ClientState.getInstance().getClient(msg.getIdentity()).write(msgToClient);
	}
	
	private void getRoomClient(ClientMessage msg) {
		Client client = ClientState.getInstance().getClient(msg.getIdentity());
		Room room = RoomManager.getInstance().getLocalRoom(client.getRoom());
		String msgToClient = ServerMessage.whoReply(
				room.getClientsListInRoom(), room.getRoomid(), room.getRoomOwner()).toJSONString();
		System.out.println(msgToClient + "send to " + msg.getIdentity());
		ClientState.getInstance().getClient(msg.getIdentity()).write(msgToClient);
	}
	
	private void createRoom(ClientMessage msg) {
		String roomOwner = msg.getIdentity();
		String roomid = msg.getString("roomid");
		if (RoomManager.getInstance().isRoomExist(roomid) || 
				!IdentityChecker.isIdentityValid(roomid)) {
			System.out.println(msg.getIdentity() + 
					" want to create a exist room or a room of unvalid name!Invalid!");
			String replyToClient = ServerMessage.createRoomReplyToClient(roomid, false).toJSONString();
			ClientState.getInstance().getClient(msg.getIdentity()).write(replyToClient);
			return;
		}
		if (ClientState.getInstance().getClient(roomOwner).getOwnedRoom() != null) {
			System.out.println(msg.getIdentity() + " alreadly owned a room!Invalid!");
			String replyToClient = ServerMessage.createRoomReplyToClient(roomid, false).toJSONString();
			ClientState.getInstance().getClient(msg.getIdentity()).write(replyToClient);
			return;
		}
		ArrayList<Conf> otherServerList = ServerState.getInstance().getServerList();
		String request = ServerMessage.lockRoomidRequest(
				ServerState.getInstance().getThisServer().getServerid(), roomid).toJSONString();
		boolean vote = true;
		try {
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
			
			for (Conf serverConf : otherServerList) {
				Socket serverSocket = new Socket(serverConf.getServerAddress(),serverConf.getCoordinationPort());
				BufferedWriter serverWriter = new BufferedWriter(
						new OutputStreamWriter(serverSocket.getOutputStream(),"UTF-8"));
				request = ServerMessage.releaseRoomidLock(
						ServerState.getInstance().getThisServer().getServerid(), roomid, vote).toJSONString();
				serverWriter.write(request);
				serverWriter.newLine();
				serverWriter.flush();
				serverWriter.close();
				serverSocket.close();
			}
			
			ClientState.getInstance().getClient(msg.getIdentity()).write(
					ServerMessage.createRoomReplyToClient(roomid, vote).toJSONString());
			
			
			if (vote) {
				Client client = ClientState.getInstance().getClient(roomOwner);
				String previousRoom = client.getRoom();
				RoomManager.getInstance().createRoom(
						roomid, roomOwner, ServerState.getInstance().getThisServer().getServerid());
				RoomManager.getInstance().joinRoom(roomOwner, previousRoom, roomid);
				client.createRoom(roomid);
				System.out.println("A new room created!");
				ClientState.getInstance().getClient(msg.getIdentity()).write(
						ServerMessage.roomChange(roomOwner, previousRoom, roomid).toJSONString());
				JSONObject inform = ServerMessage.roomChange(roomOwner, previousRoom, roomid);
				broadCastInform(msg, roomid, inform);
				broadCastInform(msg, previousRoom, inform);
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private void quitRoom(ClientMessage msg) {
		String clientid = msg.getIdentity();
		if (ClientState.getInstance().getClient(clientid) == null) {
			System.out.println(clientid + "has already quit!");
			return;
		}
		JSONObject reply = ServerMessage.roomChange(
				clientid, ClientState.getInstance().getClient(clientid).getRoom(), "");
		if (ClientState.getInstance().getClient(clientid).getOwnedRoom() == null) {
			ClientState.getInstance().getClient(clientid).write(reply.toJSONString());
			broadCastInform(msg, ClientState.getInstance().getClient(clientid).getRoom(), reply);
			RoomManager.getInstance().getLocalRoom(
					ClientState.getInstance().getClient(clientid).getRoom()).removeClient(clientid);
			ClientState.getInstance().removeClient(ClientState.getInstance().getClient(clientid));
		}
		else {
			Room room = RoomManager.getInstance().getLocalRoom(
					ClientState.getInstance().getClient(clientid).getRoom());
			JSONObject informToServer = ServerMessage.deleteRoomInform(room.getRoomServerid(), room.getRoomid());
			ArrayList<Conf> otherServerList = ServerState.getInstance().getServerList();
			String mainRoom = "MainHall-" + room.getRoomServerid();
			
			for (Conf serverConf : otherServerList) {
				try {
					Socket serverSocket = new Socket(serverConf.getServerAddress(),serverConf.getCoordinationPort());
					BufferedWriter serverWriter = new BufferedWriter(
							new OutputStreamWriter(serverSocket.getOutputStream(),"UTF-8"));
					serverWriter.write(informToServer.toJSONString());
					serverWriter.newLine();
					serverWriter.flush();
					serverWriter.close();
					serverSocket.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			broadCastInform(msg, ClientState.getInstance().getClient(clientid).getRoom(), reply);
			ArrayList<String> clientListInRoom = new ArrayList<>();
			clientListInRoom.addAll(room.getClientsListInRoom());
			clientListInRoom.remove(clientid);
			ArrayList<String> clientListInMainRoom = new ArrayList<>();
			clientListInMainRoom.addAll(RoomManager.getInstance().getLocalRoom(mainRoom).getClientsListInRoom());
			for (String clientName : clientListInRoom) {
				Client clientInRoom = ClientState.getInstance().getClient(clientName);
				clientInRoom.changeRoom(mainRoom);
				JSONObject inform = ServerMessage.roomChange(clientName, room.getRoomid(), mainRoom);
				RoomManager.getInstance().joinRoom(clientName, room.getRoomid(), mainRoom);
				clientInRoom.write(inform.toJSONString());
				broadCastInform(clientName, clientListInRoom, inform);
				broadCastInform(clientName, clientListInMainRoom, inform);
			}
		//	JSONObject inform = ServerMessage.deleteRoomToClient(room.getRoomid(), true);
		//	ClientState.getInstance().getClient(clientid).write(inform.toJSONString());
			RoomManager.getInstance().deleteRoom(room.getRoomid());
			ClientState.getInstance().getClient(clientid).deleteRoom();
			ClientState.getInstance().getClient(clientid).write(reply.toJSONString());
			ClientState.getInstance().removeClient(ClientState.getInstance().getClient(clientid));
		}
	}
	
	private void joinRoom(ClientMessage msg) {
		String clientid = msg.getIdentity();
		String roomid = msg.getString("roomid");
		Client client = ClientState.getInstance().getClient(clientid);
		String previousRoom = client.getRoom();
		
		if (client.getOwnedRoom() != null) {
			JSONObject inform = ServerMessage.roomChange(clientid, previousRoom, previousRoom);
			client.write(inform.toJSONString());
		}
		else if (RoomManager.getInstance().isLocalRoomExist(roomid)) {
			client.changeRoom(roomid);
			JSONObject inform = ServerMessage.roomChange(clientid, previousRoom, roomid);
			RoomManager.getInstance().joinRoom(clientid, previousRoom, roomid);
			client.write(inform.toJSONString());
			if (!roomid.equals(previousRoom)) {
				broadCastInform(msg, roomid, inform);
				broadCastInform(msg, previousRoom, inform);
			}
		}
		else if (RoomManager.getInstance().isRemoteRoomExist(roomid)) {
			InetAddress remoteHost = ServerState.getInstance().getRemoteServerConf(
					RoomManager.getInstance().getRemoteRoom(roomid).getRoomServerid()).getServerAddress();
			/*
			try {
				if (remoteHost.getHostAddress().equals(InetAddress.getByName("localhost"))) {
					remoteHost = InetAddress.getLocalHost();
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			*/
			int remotePort = ServerState.getInstance().getRemoteServerConf(
					RoomManager.getInstance().getRemoteRoom(roomid).getRoomServerid()).getClientsPort();
			JSONObject inform = ServerMessage.routeInfoToClient(roomid, remoteHost.getHostAddress(), remotePort);
			System.out.println(inform);
			client.write(inform.toJSONString());
			ClientState.getInstance().removeClient(client);
			RoomManager.getInstance().quitRoom(clientid, previousRoom);
			JSONObject roomchange = ServerMessage.roomChange(clientid, previousRoom, roomid);
			broadCastInform(msg, previousRoom, roomchange);
		}
		else {
			JSONObject inform = ServerMessage.roomChange(clientid, previousRoom, previousRoom);
			client.write(inform.toJSONString());
		}
	}
	
	private void deleteRoom(ClientMessage msg) {
		String clientid = msg.getIdentity();
		String roomid = msg.getString("roomid");
		Client client = ClientState.getInstance().getClient(clientid);
		
		if (RoomManager.getInstance().getLocalRoom(roomid) == null ||
				client.getOwnedRoom() == null) {
			JSONObject inform = ServerMessage.deleteRoomToClient(roomid, false);
			client.write(inform.toJSONString());
			return;
		}
		if (client.getOwnedRoom().equals(roomid)) {
			Room room = RoomManager.getInstance().getLocalRoom(roomid);
			JSONObject informToServer = ServerMessage.deleteRoomInform(room.getRoomServerid(), roomid);
			ArrayList<Conf> otherServerList = ServerState.getInstance().getServerList();
			String mainRoom = "MainHall-" + room.getRoomServerid();
			
			for (Conf serverConf : otherServerList) {
				try {
					Socket serverSocket = new Socket(serverConf.getServerAddress(),serverConf.getCoordinationPort());
					BufferedWriter serverWriter = new BufferedWriter(
							new OutputStreamWriter(serverSocket.getOutputStream(),"UTF-8"));
					serverWriter.write(informToServer.toJSONString());
					serverWriter.newLine();
					serverWriter.flush();
					serverWriter.close();
					serverSocket.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			ArrayList<String> clientListInRoom = new ArrayList<>();
			clientListInRoom.addAll(room.getClientsListInRoom());
			ArrayList<String> clientListInMainRoom = new ArrayList<>();
			clientListInMainRoom.addAll(RoomManager.getInstance().getLocalRoom(mainRoom).getClientsListInRoom());
			for (String clientName : clientListInRoom) {
				Client clientInRoom = ClientState.getInstance().getClient(clientName);
				clientInRoom.changeRoom(mainRoom);
				JSONObject inform = ServerMessage.roomChange(clientName, roomid, mainRoom);
				RoomManager.getInstance().joinRoom(clientName, roomid, mainRoom);
				clientInRoom.write(inform.toJSONString());
				broadCastInform(clientName, clientListInRoom, inform);
				broadCastInform(clientName, clientListInMainRoom, inform);
			}
			JSONObject inform = ServerMessage.deleteRoomToClient(roomid, true);
			client.write(inform.toJSONString());
			RoomManager.getInstance().deleteRoom(roomid);
			client.deleteRoom();
		}
		else {
			JSONObject inform = ServerMessage.deleteRoomToClient(roomid, false);
			client.write(inform.toJSONString());
		}
		
	}
	
	private void broadCastInform(String clientName, ArrayList<String> clientList, JSONObject inform) {
		for (String clientid : clientList) {
			if (!clientid.equals(clientName)) {
				ClientState.getInstance().getClient(clientid).write(
						inform.toJSONString());
			}
		}
	}
	
/*
	private void broadCastInform(String clientName, String roomid, JSONObject inform) {
		ArrayList<String> clientList = ClientState.getInstance().getAllClientList();
		for (String clientid : clientList) {
			if (!clientid.equals(clientName)) {
				if(ClientState.getInstance().getClient(clientid).getRoom().equals(roomid)) {
					ClientState.getInstance().getClient(clientid).write(
							inform.toJSONString());
				}
			}
		}
	}
*/
	
	private void broadCastInform(ClientMessage msg, String roomid, JSONObject inform) {
		ArrayList<String> clientList = ClientState.getInstance().getAllClientList();
		for (String clientid : clientList) {
			if (!clientid.equals(msg.getIdentity())) {
				if(ClientState.getInstance().getClient(clientid).getRoom().equals(roomid)) {
					ClientState.getInstance().getClient(clientid).write(
							inform.toJSONString());
				}
			}
		}
	}
	
	private void broadCastMsgInRoom(ClientMessage msg) {
		
		ArrayList<String> clientList = ClientState.getInstance().getAllClientList();
		for (String clientid : clientList) {
			if (!clientid.equals(msg.getIdentity())) {
				if(ClientState.getInstance().getClient(clientid).getRoom().equals(
						ClientState.getInstance().getClient(msg.getIdentity()).getRoom())) {
					ClientState.getInstance().getClient(clientid).write(
							ServerMessage.message(msg.getIdentity(), msg.getString("content")).toJSONString());
				}
			}
		}
/*		
		ArrayList<Client> clientList = ClientState.getInstance().getClientList();
		for (Client client : clientList) {
			if(!client.getClientid().equals(clientid)) {
				if (client.getRoom().equals(roomid)) {
					ClientState.getInstance().getClient(client).write(msg);
				}
			}
		}
*/
	
	}

}
