package myServer2;

/*
 * Name : Min Gao, Lang Lin, Ziang Xu, Xing Jiang
 * COMP90015 Distributed Systems 2016 SM2 
 * Project2-Extended Multi-Server Chat System  
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AuthenLoader {
	
	public ArrayList<String> loadUserNameList(String authenPath) {
		ArrayList<String> userNameList = new ArrayList<>();
		ArrayList<String> authenInfoList = readAuthenInfo(authenPath);
		for (String authenInfo : authenInfoList) {
			userNameList.add(authenInfo.split("\t")[0]);
		}
		return userNameList;
	}
	
	public ArrayList<String> loadPasswordList(String authenPath) {
		ArrayList<String> passwordList = new ArrayList<>();
		ArrayList<String> authenInfoList = readAuthenInfo(authenPath);
		for (String authenInfo : authenInfoList) {
			passwordList.add(authenInfo.split("\t")[1]);
		}
		return passwordList;
	}
	
	
	private ArrayList<String> readAuthenInfo(String authenPath) {
		ArrayList<String> authenInfo = new ArrayList<>();
		try {
			Scanner inputAuthenInfo = new Scanner(new FileInputStream(authenPath));
			while (inputAuthenInfo.hasNextLine()) {
				authenInfo.add(inputAuthenInfo.nextLine());
			}
			inputAuthenInfo.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return authenInfo;
	}

}
