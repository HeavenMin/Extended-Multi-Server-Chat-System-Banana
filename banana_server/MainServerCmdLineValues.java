package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import org.kohsuke.args4j.Option;

public class MainServerCmdLineValues {
	
	@Option(required = true, name = "-cp", aliases = {"--mainServerClientPort"}, usage = "the clients port")
	private int mainServerClientPort;
	
	@Option(required = true, name = "-sp", aliases = {"--mainServerServerPort"}, usage = "the coordination port")
	private int mainServerServerPort;
	
	public int getMainServerClientPort() {
		return this.mainServerClientPort;
	}
	
	public int getMainServerServerPort() {
		return this.mainServerServerPort;
	}	

}
