package ChatGUI;

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
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Client {

	private static Scanner keyboard;

	public static void main(String[] args) throws IOException, ParseException {
		keyboard = new Scanner(System.in);
		String userName;	
		String password;
		String isApproved = null;
		SSLSocket mainServerSocket = null;
//		DataOutputStream mainServerOut = null;
		BufferedWriter writer = null;
		BufferedReader reader = null;
		System.setProperty("javax.net.ssl.keyStore","kserver.keystore");
		System.setProperty("javax.net.ssl.trustStore", "tclient.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
//		System.setProperty("javax.net.debug","all");
		SSLSocket socket = null;
		String identity = null;
		boolean debug = false;
		try {
			//load command line args
			ComLineValues values = new ComLineValues();
			CmdLineParser parser = new CmdLineParser(values);
			try {
				parser.parseArgument(args);
				String hostname = values.getHost();
				identity = values.getIdeneity();
				int port = values.getPort();
				debug = values.isDebug();
				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				mainServerSocket = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
//				mainServerOut = new DataOutputStream(mainServerSocket.getOutputStream());
				reader = new BufferedReader(
						new InputStreamReader(mainServerSocket.getInputStream(), "UTF-8"));
				writer = new BufferedWriter(
						new OutputStreamWriter(mainServerSocket.getOutputStream(), "UTF-8"));
			} catch (CmdLineException e) {
				System.out.println("using parameter -h <hostaddress> -p <port> -i <identity>");
				e.printStackTrace();
			}
			
			State state = new State(identity, "");
			
			System.out.println("Please enter your username: ");
			userName = keyboard.nextLine();
			System.out.println("Please enter your password: ");
			password = keyboard.nextLine();
			
			writer.write(ClientMessages.sendUsernameAndPassword(userName, password).toJSONString());
			writer.newLine();
			writer.flush();
			
			String feedback = reader.readLine();
			
			JSONParser jsonparser = new JSONParser();
			JSONObject feedbackJsonObj = (JSONObject) jsonparser.parse(feedback);
			String feedbackType = (String) feedbackJsonObj.get("type");
			if (feedbackType.equals("clientAuthen")) {
				isApproved = (String) feedbackJsonObj.get("approved");
				
				System.out.println("the request to the chat server is :" + isApproved);
				//System.out.println(feedback);	//for test
				
				if (isApproved.equals("true")) {
					JSONArray serverIdArray = (JSONArray) feedbackJsonObj.get("serveridArray");
					JSONArray serverAddressArray = (JSONArray) feedbackJsonObj.get("serverAddressArray");
					JSONArray clientsPortArray = (JSONArray) feedbackJsonObj.get("clientsPortArray");
					for (int i = 0; i < serverIdArray.size(); i++) {
						System.out.println( (i + 1) + ":"+ serverIdArray.get(i).toString());
					}
					
					System.out.println("Please choose a server you want to connect!(enter a number):");
					int serverNumber = keyboard.nextInt() - 1;

					String newServerAddress = (String) serverAddressArray.get(serverNumber);
					int newClientPort = Integer.parseInt((String) clientsPortArray.get(serverNumber));
					
				//	System.out.println(newServerAddress);	//just for test
				//	System.out.println(newClientPort);		//just for test
					
					SSLSocketFactory sslsocketfactory2 = (SSLSocketFactory) SSLSocketFactory.getDefault();
					socket = (SSLSocket) sslsocketfactory2.createSocket(newServerAddress, newClientPort);
					mainServerSocket.close();
				}
				else {
					System.out.println("The username or the password is not correct!.");
					System.exit(1);
				}
			}
			else {
				System.exit(1);
			}

			
			if (isApproved.equals("true")) {
				// start sending thread
				ChatGUI gui = new ChatGUI();
				MessageSendThread messageSendThread = new MessageSendThread(socket, state, debug,gui);
				Thread sendThread = new Thread(messageSendThread);
				sendThread.start();
				// start receiving thread
				Thread receiveThread = new Thread(new MessageReceiveThread(socket, state, messageSendThread, debug, gui));
				receiveThread.start();
			}
			
			
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
		} catch (IOException e) {
			System.out.println("Communication Error: " + e.getMessage());
		}
	}
}
