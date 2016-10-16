package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientMessage {
	private JSONObject jsonMessage;
	private String identity;
	
	public ClientMessage(String msg, String identity) {
		this.identity = identity;
		JSONParser parser = new JSONParser();
		try {
			this.jsonMessage = (JSONObject) parser.parse(msg);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject getJsonMessage() {
		return jsonMessage;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public String getMessageType() {
		return (String) jsonMessage.get("type");
	}
	
	public String getString(String key) {
		return (String) jsonMessage.get(key);
	}

}
