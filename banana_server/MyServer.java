package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class MyServer {
	
	public static void main(String[] args) throws IOException, ParseException {
		
		//get command line values from -n and -l
		MyServerCmdLineValues serverValues = new MyServerCmdLineValues();
		CmdLineParser parser = new CmdLineParser(serverValues);
		ConfLoader confloader = new ConfLoader();
		Conf server = null;
		
	//	ServerSocket listeningClientSocket = null;
	//	Socket clientSocket = null;
		
		try {
			parser.parseArgument(args);
			//serverid = serverValues.getServerid();
			String serverConfPath = serverValues.getServerConf();
			
			server = confloader.loadConf(serverValues.getServerid(), serverConfPath);
			if (server != null) {
	//			listeningClientSocket = new ServerSocket(server.getClientsPort());
				//other server state
				confloader.initializeServerState(serverValues.getServerid(), serverConfPath);
				ServerState.getInstance().setThisServer(server);
				RoomManager.getInstance().createRoom(
						"MainHall-" + serverValues.getServerid(), "", serverValues.getServerid());
				
				System.out.println("        Serverid = " + server.getServerid());	//for test
				System.out.println("   ServerAddress = " + server.getServerAddress());	//for test
				System.out.println("     ClientsPort = " + server.getClientsPort());	//for test
				System.out.println("CoordinationPort = " + server.getCoordinationPort());	//for test
				System.out.println("Total server number: " +
						(ServerState.getInstance().getServerList().size() + 1));
				System.out.println("The server " + server.getServerid() + " is running!");
				
			}
			else {
				System.out.println("Do not have this server in the confguiration!");
				System.exit(0);
			}
			
			new NewIdentityCheckerThread(server.getClientsPort(), server.getServerid()).start();
			new ServerCommunicationThread(server.getCoordinationPort()).start();
			new ClientMessageReader().start();
			new ClientConnection().start();
			

		}
		catch (CmdLineException e) {
			e.printStackTrace();
		}
		/*
		catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
}
