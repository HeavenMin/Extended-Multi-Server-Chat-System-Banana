package myServer2;

/*
 * Name : Min Gao
 * COMP90015 Distributed Systems 2016 SM2 
 * Project1-Multi-Server Chat System  
 * Login Name : ming1 
 * Student Number : 773090 
 */

import java.util.regex.Pattern;

public class IdentityChecker {
	
	static Pattern validIdentity = Pattern.compile("^[a-zA-Z]\\w{2,15}$");
	
	public static boolean isIdentityValid(String identity) {
		return validIdentity.matcher(identity).find();
	}

}
