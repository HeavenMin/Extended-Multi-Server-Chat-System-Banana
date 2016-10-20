package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.net.ssl.SSLSocket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerMsgDealerThread extends Thread {
	private SSLSocket serverSocket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private JSONParser parser = new JSONParser();
	volatile private boolean isRunning = true;

	public ServerMsgDealerThread(SSLSocket serverSocket) {
		try {
			this.serverSocket = serverSocket;
			System.out.println("A new server socket created!");// just for test
			this.reader = new BufferedReader(
					new InputStreamReader(this.serverSocket.getInputStream(), "UTF-8"));
			this.writer = new BufferedWriter(
					new OutputStreamWriter(this.serverSocket.getOutputStream(), "UTF-8"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {
			while(isRunning) {
				String msg = reader.readLine();
				System.out.println("Receive a msg from another server" + msg);
				JSONObject msgJsonObj = (JSONObject) parser.parse(msg);
				String msgType = (String) msgJsonObj.get("type");

				if (msgType.equals("lockidentity")) {
					String clientid = (String) msgJsonObj.get("identity");
					boolean vote = !ClientState.getInstance().isClientidExist(clientid);
					if (vote) {
						ClientState.getInstance().addLockClient(clientid);
						System.out.println("Allow the new identity: " + clientid);
					}
					msg = ServerMessage.lockIdentityReply(
							ServerState.getInstance().getThisServer().getServerid(), clientid, vote).toJSONString();
					writer.write(msg);
					writer.newLine();
					writer.flush();
				}

				if (msgType.equals("releaseidentity")) {
					System.out.println("relealse a identity!");//for test
					String clientid = (String) msgJsonObj.get("identity");
					ClientState.getInstance().releaseLockClient(clientid);
					isRunning = false;
				}

				if (msgType.equals("lockroomid")) {
					String room = (String) msgJsonObj.get("roomid");
					boolean vote = !RoomManager.getInstance().isRoomExist(room);
					if (vote) {
						RoomManager.getInstance().lockRoom(room);
						System.out.println("Allow the new roomid: " + room);
					}
					msg = ServerMessage.lockRoomidReply(
							ServerState.getInstance().getThisServer().getServerid(), room, vote).toJSONString();
					writer.write(msg);
					writer.newLine();
					writer.flush();
				}

				if (msgType.equals("releaseroomid")) {
					System.out.println("get a release roomid!");
					String room = (String) msgJsonObj.get("roomid");
					RoomManager.getInstance().releaseLockRoom(room);
					if (((String) msgJsonObj.get("approved")).equals("true")) {
						String serverid = (String) msgJsonObj.get("serverid");
						RoomManager.getInstance().addOtherServerRoom(room, serverid);
					}
				}

				if (msgType.equals("deleteroom")) {
					System.out.println("A remote room was deleted!");
					String remoteRoom = (String) msgJsonObj.get("roomid");
					RoomManager.getInstance().removeOtherServerRoom(remoteRoom);

				}
				
				if (msgType.equals("newserver")) {
					String serverid = (String) msgJsonObj.get("serverid");
					String serverAddress = (String) msgJsonObj.get("serverAddress");
					int clientsPort = Integer.parseInt((String) msgJsonObj.get("clientsPort"));
					int coordinationPort = Integer.parseInt((String) msgJsonObj.get("coordinationPort"));
					Conf newComeServer = new Conf(serverid, serverAddress, clientsPort, coordinationPort);
					ServerState.getInstance().serverConnected(newComeServer);
					RoomManager.getInstance().addOtherServerRoom("MainHall-" + serverid, serverid);
				}
				
			//	System.out.println("want enter the heart");
				if (msgType.equals("heartbeatfail")) {
				//	System.out.println("before1 : " + ServerState.getInstance().getServerList().size());
					String serverid = (String) msgJsonObj.get("failserver");
					JSONArray roomIdArray = (JSONArray) msgJsonObj.get("rooms");
				//	System.out.println("before2 : " + ServerState.getInstance().getServerList().size());
					ServerState.getInstance().serverDisconnected(ServerState.getInstance().getRemoteServerConf(serverid));
					System.out.println("need to delete this room : " + roomIdArray.toJSONString());
					for (int i = 0; i < roomIdArray.size(); i++) {
						RoomManager.getInstance().removeOtherServerRoom(roomIdArray.get(i).toString());
					}
					
				//	System.out.println("after : " + ServerState.getInstance().getServerList().size());
					
				}
				
				isRunning = false;
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
