package com.globecapital.jobs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


import com.globecapital.config.AppConfig;
import com.msf.log.Logger;

public class Server {
	
	private static Logger log;
	
	public static void main(String[] args){
		String config_file = args[0];
		try{
			
			Properties JSLogProperties = new Properties();
			AppConfig.loadFile(config_file);
			FileInputStream stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(Server.class);
			log.info("inside code");
			System.out.println("inside code");
		ServerSocket ss=new ServerSocket(4501); 
		log.info("socket initialised");
		System.out.println("socket initialised");
		Socket s=ss.accept();//establishes connection  
		log.info("connection established at "+s);
		System.out.println("connection established at "+s);
		DataInputStream dis=new DataInputStream(s.getInputStream()); 
		String  str=(String)dis.readUTF();  
		System.out.println("message= "+str);  
		log.info("Message= "+str);
		System.out.println("Message= "+str);
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
		dout.writeUTF("Hello Client");
		dout.flush();  
		dout.close(); 
		ss.close();  
		}catch(Exception e)
		{
			log.info("error");
			System.out.println("error");
			}  
		} 
}

