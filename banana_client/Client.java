package MyClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

//haha
public class Client {

	private static Scanner keyboard;

	public static void main(String[] args) throws IOException, ParseException {
		keyboard = new Scanner(System.in);
		String userName;
		String password;
		String isApproved = null;
		Socket mainServerSocket = null;
		DataOutputStream mainServerOut = null;
		BufferedReader reader = null;

		Socket socket = null;
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
				mainServerSocket = new Socket(hostname, port);
				mainServerOut = new DataOutputStream(mainServerSocket.getOutputStream());
				reader = new BufferedReader(
						new InputStreamReader(mainServerSocket.getInputStream(), "UTF-8"));
			} catch (CmdLineException e) {
				e.printStackTrace();
			}

			State state = new State(identity, "");

			System.out.println("Please enter your username: ");
			userName = keyboard.nextLine();
			System.out.println("Please enter your password: ");
			password = keyboard.nextLine();

			mainServerOut.write((ClientMessages.sendUsernameAndPassword(userName, password).toJSONString() + "\n").getBytes("UTF-8"));
			mainServerOut.flush();

			String feedback = reader.readLine();

			JSONParser jsonparser = new JSONParser();
			JSONObject feedbackJsonObj = (JSONObject) jsonparser.parse(feedback);
			String feedbackType = (String) feedbackJsonObj.get("type");
			if (feedbackType.equals("clientAuthen")) {
				isApproved = (String) feedbackJsonObj.get("approved");
				if (isApproved.equals("true")) {
					JSONArray serverIdArray = (JSONArray) feedbackJsonObj.get("serverIdArray");
					JSONArray serverAddressArray = (JSONArray) feedbackJsonObj.get("serverAddressArray");
					JSONArray clientsPortArray = (JSONArray) feedbackJsonObj.get("clientsPortArray");
					for (int i = 0; i < serverIdArray.size(); i++) {
						System.out.println(i + ":"+ serverIdArray.get(i).toString());
					}
					System.out.println("Please choose a sever you want to connect!(enter a number):");
					int serverNumber = keyboard.nextInt();
					String newServerAddress = (String) serverAddressArray.get(serverNumber);
					int newClientPort = Integer.parseInt((String) clientsPortArray.get(serverNumber));
					socket = new Socket(newServerAddress, newClientPort);
					mainServerSocket.close();
				}
				else {
					System.exit(1);
				}
			}
			else {
				System.exit(1);
			}


			if (isApproved.equals("true")) {
				// start sending thread
				MessageSendThread messageSendThread = new MessageSendThread(socket, state, debug);
				Thread sendThread = new Thread(messageSendThread);
				sendThread.start();

				// start receiving thread
				Thread receiveThread = new Thread(new MessageReceiveThread(socket, state, messageSendThread, debug));
				receiveThread.start();
			}


		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
		} catch (IOException e) {
			System.out.println("Communication Error: " + e.getMessage());
		}
	}
}
