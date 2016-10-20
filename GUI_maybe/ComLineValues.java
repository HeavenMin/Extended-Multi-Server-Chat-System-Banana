package ChatGUI;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import org.kohsuke.args4j.Option;


public class ComLineValues {
	@Option(required=false, name = "-h", aliases="--host", usage="Server host address")
	private String host = "172.16.42.4";	//default host address
	
	@Option(required=false, name="-p", aliases="--port", usage="Server port number")
	private int port = 80;

	@Option(required=true, name = "-i", aliases="--identity", usage="Client identity")
	private String identity;
	
	@Option(required=false, name = "-d", aliases="--debug", usage="Debug mode")
	private boolean debug = false;
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public String getIdeneity() {
		return identity;
	}
	
	public boolean isDebug() {
		return debug;
	}
}
