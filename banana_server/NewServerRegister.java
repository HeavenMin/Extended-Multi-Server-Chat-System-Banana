package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NewServerRegister extends Thread{

	private MainServerInfo mainServer;
//	private BufferedWriter writer;
//	private BufferedReader reader;
//	private String mainServerAddress;
//	private int mainServerPort;
	
	private Conf myServer;
	volatile private boolean isRunning = true;
	
	public NewServerRegister(Conf myServer, MainServerInfo mainServer) {
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		this.myServer = myServer;
		
		this.mainServer = mainServer;
	//	this.mainServerAddress = mainServerAddress;
	//	this.mainServerPort = mainServerPort;
	//	SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	//	try {
	//		mainServerSocket = (SSLSocket) sslsocketfactory.createSocket(
	//									this.mainServerAddress, this.mainServerPort);
	//		reader = new BufferedReader(
	//				new InputStreamReader(this.mainServerSocket.getInputStream(), "UTF-8"));
	//		writer = new BufferedWriter(
	//				new OutputStreamWriter(this.mainServerSocket.getOutputStream(), "UTF-8"));
	//	} catch (UnknownHostException e) {
	//		
	//		e.printStackTrace();
	//	} catch (IOException e) {
	//		
	//		e.printStackTrace();
	//	}
	}
	
	
	@Override
	public void run() {
		
		try {
			
			mainServer.write(ServerMessage.addServerRequest(myServer).toJSONString());
			
	//		writer.write(ServerMessage.addServerRequest(myServer).toJSONString());
	//		writer.newLine();
	//		writer.flush();	
			
			String reply = mainServer.read();
			
	//		String reply = reader.readLine();
	//		System.out.println("debug");
	//		System.out.println(reply);
			
			JSONParser parser = new JSONParser();
			JSONObject msgJsonObj = (JSONObject) parser.parse(reply);
			String replyType = (String) msgJsonObj.get("type");
			if (replyType.equals("serverreply")) {
				JSONArray serverIdArray = (JSONArray) msgJsonObj.get("serveridArray");
				JSONArray serverAddressArray = (JSONArray) msgJsonObj.get("serverAddressArray");
				JSONArray clientsPortArray = (JSONArray) msgJsonObj.get("clientsPortArray");
				JSONArray coordinationPortArray = (JSONArray) msgJsonObj.get("coordinationPortArray");
				JSONArray roomArray = (JSONArray) msgJsonObj.get("roomArray");
				JSONArray roomServeridArray = (JSONArray) msgJsonObj.get("roomBelongingServerArray");
				
				
				for (int i = 0; i < serverIdArray.size(); i++) {
					ServerState.getInstance().serverConnected(new Conf(serverIdArray.get(i).toString(), serverAddressArray.get(i).toString(),
							Integer.parseInt(clientsPortArray.get(i).toString()),
							Integer.parseInt(coordinationPortArray.get(i).toString())));
				//	RoomManager.getInstance().addOtherServerRoom(
				//			"MainHall-" + serverIdArray.get(i).toString() , serverIdArray.get(i).toString());
				}
				
				for (int i = 0; i < roomArray.size(); i++) {
					RoomManager.getInstance().addOtherServerRoom(roomArray.get(i).toString(), roomServeridArray.get(i).toString());
				}
			}
			
			while (isRunning) {
				try {
					mainServer.write(ServerMessage.checkBeat().toJSONString());
					
		//			writer.write(ServerMessage.checkBeat().toJSONString());
		//			writer.newLine();
		//			writer.flush();	
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		
	}

}
