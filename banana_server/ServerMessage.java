package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import java.net.InetAddress;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class ServerMessage {

	public static JSONObject lockIdentityRequest(String serverid, String identity) {
		JSONObject lockIdentity = new JSONObject();
		lockIdentity.put("type", "lockidentity");
		lockIdentity.put("serverid", serverid);
		lockIdentity.put("identity", identity);
		return lockIdentity;

	}

	public static JSONObject lockIdentityReply(String serverid, String identity, boolean decide) {
		JSONObject reply = new JSONObject();
		reply.put("type", "lockidentity");
		reply.put("serverid", serverid);
		reply.put("identity", identity);
		reply.put("locked", decide ? "true" : "false");
		return reply;
	}

	public static JSONObject newIdentityReplyToClient(boolean decide) {
		JSONObject reply = new JSONObject();
		reply.put("type", "newidentity");
		reply.put("approved", decide ? "true" : "false");
		return reply;
	}

	public static JSONObject releaseIdentityLock(String serverid, String identity) {
		JSONObject release = new JSONObject();
		release.put("type", "releaseidentity");
		release.put("serverid", serverid);
		release.put("identity", identity);
		return release;

	}

	public static JSONObject roomChange(String identity, String former, String roomidNow) {
		JSONObject roomchange = new JSONObject();
		roomchange.put("type", "roomchange");
		roomchange.put("identity", identity);
		roomchange.put("former", former);
		roomchange.put("roomid", roomidNow);
		return roomchange;
	}

	public static JSONObject roomListReply(ArrayList<String> roomList) {
		JSONObject reply = new JSONObject();
		JSONArray list = new JSONArray();
		for (String room : roomList) {
			list.add(room);
		}
		reply.put("type", "roomlist");
		reply.put("rooms", list);
		return reply;
	}

	public static JSONObject whoReply(ArrayList<String> clients, String roomid, String owner) {
		JSONObject who = new JSONObject();
		JSONArray list = new JSONArray();
		for (String client : clients) {
			list.add(client);
		}
		who.put("type", "roomcontents");
		who.put("roomid", roomid);
		who.put("identities", list);
		who.put("owner", owner);
		return who;
	}

	public static JSONObject lockRoomidRequest(String serverid, String roomid) {
		JSONObject lockRoomid = new JSONObject();
		lockRoomid.put("type", "lockroomid");
		lockRoomid.put("serverid", serverid);
		lockRoomid.put("roomid", roomid);
		return lockRoomid;
	}

	public static JSONObject lockRoomidReply(String serverid, String roomid, boolean decide) {
		JSONObject reply = new JSONObject();
		reply.put("type", "lockroomid");
		reply.put("serverid", serverid);
		reply.put("roomid", roomid);
		reply.put("locked", decide ? "true" : "false");
		return reply;
	}


	public static JSONObject releaseRoomidLock(String serverid, String roomid, boolean decide) {
		JSONObject release = new JSONObject();
		release.put("type", "releaseroomid");
		release.put("serverid", serverid);
		release.put("roomid", roomid);
		release.put("approved", decide ? "true" : "false");
		return release;
	}

	public static JSONObject createRoomReplyToClient(String roomid, boolean decide) {
		JSONObject reply = new JSONObject();
		reply.put("type", "createroom");
		reply.put("roomid", roomid);
		reply.put("approved", decide ? "true" : "false");
		return reply;
	}

	public static JSONObject routeInfoToClient(String roomid, String hostAddress, int port) {
		JSONObject reply = new JSONObject();
		reply.put("type", "route");
		reply.put("roomid", roomid);
		reply.put("host", hostAddress);
		reply.put("port", Integer.toString(port));
		return reply;
	}

	public static JSONObject serverChangeToClient(String serverid, boolean decide) {
		JSONObject reply = new JSONObject();
		reply.put("type", "serverchange");
		reply.put("approved", decide ? "true" : "false");
		reply.put("serverid", serverid);
		return reply;
	}

	public static JSONObject deleteRoomToClient(String roomid, boolean decide) {
		JSONObject reply = new JSONObject();
		reply.put("type", "deleteroom");
		reply.put("roomid", roomid);
		reply.put("approved", decide ? "true" : "false");
		return reply;
	}

	public static JSONObject deleteRoomInform(String serverid, String roomid) {
		JSONObject inform = new JSONObject();
		inform.put("type", "deleteroom");
		inform.put("serverid", serverid);
		inform.put("roomid", roomid);
		return inform;
	}

	public static JSONObject message(String clientid, String content) {
		JSONObject message = new JSONObject();
		message.put("type", "message");
		message.put("identity", clientid);
		message.put("content", content);
		return message;
	}

	public static JSONObject serverChange(String serverid, boolean decide) {
		JSONObject serverChange = new JSONObject();
		serverChange.put("type", "serverchange");
		serverChange.put("approved", decide ? "true" : "false");
		serverChange.put("serverid", serverid);
		return serverChange;
	}

	public static JSONObject joinRoom(String roomid) {
		JSONObject join = new JSONObject();
	    join.put("type", "join");
	    join.put("roomid", roomid);
	    return join;
	}

	public static JSONObject quit() {
	    JSONObject quit = new JSONObject();
	    quit.put("type", "quit");
	    return quit;
	  }

	//这个方法是main server用来回复user他们用username,password登陆成功与否
	public static JSONObject userLogin(boolean approved,ArrayList<Conf> serverConf){
		JSONObject userLogin = new JSONObject();
		userLogin.put("type", "clientAuthen");
		if(approved){
			userLogin.put("approved","true");
			JSONArray serverid_array= new JSONArray();
			JSONArray serverAddress_array= new JSONArray();
			JSONArray clientsPort_array= new JSONArray();
			for(int i=0;i<serverConf.size();i++){
				serverid_array.add(serverConf.get(i).getServerid());
				serverAddress_array.add(serverConf.get(i).getServerAddress().getHostAddress());
				clientsPort_array.add(Integer.toString(serverConf.get(i).getClientsPort()));
			}
			userLogin.put("serveridArray", serverid_array);
			userLogin.put("serverAddressArray", serverAddress_array);
			userLogin.put("clientsPortArray", clientsPort_array);
		}
		else{
			userLogin.put("approved","false");
		}
		return userLogin;
	}

	public static JSONObject addServerRequest(Conf serverConf){
		String serverid = serverConf.getServerid();
		InetAddress serverAddress = serverConf.getServerAddress();
		int clientsPort = serverConf.getClientsPort();
		int coordinationPort = serverConf.getCoordinationPort();
		JSONObject request = new JSONObject();
		request.put("type", "addserver");
		request.put("serverid",serverid);
		request.put("serverAddress", serverAddress.getHostAddress());
		request.put("clientsPort", Integer.toString(clientsPort));
		request.put("coordinationPort", Integer.toString(coordinationPort));

		return request;
	}

	public static JSONObject newServerReply(ArrayList<Conf> serverConfs,ArrayList<String> rooms,ArrayList<String> rooms_belong){
		String[] serverid = new String[serverConfs.size()];
		InetAddress[] serverAddress = new InetAddress[serverConfs.size()];
		int[] clientsPort = new int[serverConfs.size()];
		int[] coordinationPort = new int[serverConfs.size()];
		for(int i=0;i<serverConfs.size();i++){
			serverid[i] = serverConfs.get(i).getServerid();
			serverAddress[i] = serverConfs.get(i).getServerAddress();
			clientsPort[i] = serverConfs.get(i).getClientsPort();
			coordinationPort[i] = serverConfs.get(i).getCoordinationPort();
		}
		JSONObject reply = new JSONObject();
		reply.put("type", "serverreply");
		JSONArray serverid_array = new JSONArray();
		JSONArray serverAddress_array = new JSONArray();
		JSONArray clientsPort_array = new JSONArray();
		JSONArray coordinationPort_array = new JSONArray();
		JSONArray room_array = new JSONArray();
		JSONArray room_belonging_server_array = new JSONArray();
		for(int i=0; i<serverid.length; i++){
			serverid_array.add(serverid[i]);
			serverAddress_array.add(serverAddress[i].getHostAddress());
			clientsPort_array.add(clientsPort[i]);
			coordinationPort_array.add(coordinationPort[i]);
		}
		for(int i=0;i<rooms.size();i++){
			room_array.add(rooms.get(i));
			room_belonging_server_array.add(rooms_belong.get(i));
		}
		reply.put("serveridArray", serverid_array);
		reply.put("serverAddressArray", serverAddress_array);
		reply.put("clientsPortArray", clientsPort_array);
		reply.put("coordinationPortArray", coordinationPort_array);
		reply.put("roomArray", room_array);
		reply.put("roomBelongingServerArray",room_belonging_server_array);
		return reply;
	}


	public static JSONObject noticeNewServerComing(Conf serverConf){
		String serverid = serverConf.getServerid();
		InetAddress serverAddress = serverConf.getServerAddress();
		int clientsPort = serverConf.getClientsPort();
		int coordinationPort = serverConf.getCoordinationPort();
		JSONObject newServer = new JSONObject();
		newServer.put("type", "newserver");
		newServer.put("serverid",serverid);
		newServer.put("serverAddress", serverAddress.getHostAddress());
		newServer.put("clientsPort", Integer.toString(clientsPort));
		newServer.put("coordinationPort", Integer.toString(coordinationPort));
		return newServer;
	}

	public static JSONObject notifyOneServerDown(Conf serverConf,ArrayList<String> room_name){
		JSONObject heartbeatfail = new JSONObject();
		JSONArray rooms = new JSONArray();
		for(int i=0;i<room_name.size();i++){
			rooms.add(room_name.get(i));
		}
		heartbeatfail.put("type", "heartbeatfail");
		heartbeatfail.put("failserver", serverConf.getServerid());
		heartbeatfail.put("rooms", rooms);
		return heartbeatfail;
	}

	public static JSONObject addRoom(String roomid,String serverid){
		JSONObject addroom = new JSONObject();
		addroom.put("type", "addroom");
		addroom.put("roomID", roomid);
		addroom.put("serverID", serverid);
		return addroom;
	}

	public static JSONObject deleteRoom(String roomid){
		JSONObject deleteroom = new JSONObject();
		deleteroom.put("type", "deleteroom");
		deleteroom.put("roomID", roomid);
		return deleteroom;
	}
	
	public static JSONObject checkBeat() {
		JSONObject checkbeat = new JSONObject();
		checkbeat.put("type", "checkbeat");
		return checkbeat;
	}
	
}