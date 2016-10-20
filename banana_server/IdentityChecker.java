package myServer3;

/*
 * Name : Min Gao, Lang Lin, Xing Jiang, Ziang Xu
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import java.util.regex.Pattern;

public class IdentityChecker {
	
	static Pattern validIdentity = Pattern.compile("^[a-zA-Z]\\w{2,15}$");
	
	public static boolean isIdentityValid(String identity) {
		return validIdentity.matcher(identity).find();
	}

}
