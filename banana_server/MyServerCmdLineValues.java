package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import org.kohsuke.args4j.Option;

public class MyServerCmdLineValues {
	
	@Option(required = true, name = "-n", aliases = {"--serverid"}, usage = "name of the server")
	private String serverid;
	
	@Option(required = true, name = "-sa", aliases = {"--serverAddress"}, usage = "address of the server")
	private String serveraddress;
	
	@Option(required = true, name = "-cp", aliases = {"--clientsport"}, usage = "then clients port")
	private int clientsPort;
	
	@Option(required = true, name = "-sp", aliases = {"--coordinationport"}, usage = "then coordination port")
	private int coordinationPort;
	
	@Option(required = true, name = "-ma", aliases = {"--mainserveraddress"}, usage = "the ip address of main server")
	private String mainServerAddress;
	
	@Option(required = false, name = "-mp", aliases = {"--mainserverport"}, usage = "the port of main server")
	private int mainServerPort = 90;
	
	public String getServerid() {
		return this.serverid;
	}
	
	public String getServerAddress() {
		return this.serveraddress;
	}
	
	public int getClientsPort() {
		return this.clientsPort;
	}
	
	public int getCoordinationPort() {
		return this.coordinationPort;
	}
	
	public String getMainServerAddress() {
		return this.mainServerAddress;
	}
	
	public int getMainServerPort() {
		return this.mainServerPort;
	}
	
	/*
	@Option(required = true, name = "-l", aliases = {"--serverConfPath"}, usage = "the path of configuration of server")
	private String serverConfPath;
	
	public String getServerConf() {
		return this.serverConfPath;
	}
	*/
}