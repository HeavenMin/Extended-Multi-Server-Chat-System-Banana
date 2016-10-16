package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import org.kohsuke.args4j.Option;

public class MyServerCmdLineValues {
	
	@Option(required = true, name = "-n", aliases = {"--serverid"}, usage = "name of the server")
	private String serverid;
	
	@Option(required = true, name = "-l", aliases = {"--serverConfPath"}, usage = "the path of configuration of server")
	private String serverConfPath;
	
	public String getServerid() {
		return this.serverid;
	}
	
	public String getServerConf() {
		return this.serverConfPath;
	}
}