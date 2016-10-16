package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

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

}
