package com.globecapital.jobs;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;

import com.globecapital.config.AppConfig;
import com.globecapital.services.omex.GetOMEXMessageResponse;
import com.msf.log.Logger;

public class OmexServer {
	
	private static Logger log;

	
	static ServerSocket ss;
//	static HashMap<String,Set<Socket>> map=new HashMap<String,Set<Socket>>();
	static HashMap<String,Socket> map=new HashMap<String,Socket>();
	
	public static Set<String> clientData = new HashSet<String>();

	
	static {
		try {
			ss=new ServerSocket(4501);
//			log.info("OMEX Server initialised");
			System.out.println("OMEX Server initialised");
			
		} catch (IOException e) {
//			log.info("Server not initialised");
			System.out.println("Server not initialised");
			e.printStackTrace();
		}
	
	}
	
	public static void main(String[] args) throws Exception {
		String config_file = args[0];
		Properties JSLogProperties = new Properties();
		AppConfig.loadFile(config_file);
		FileInputStream stream = new FileInputStream(config_file);
		JSLogProperties.load(stream);
		Logger.setLogger(JSLogProperties);
		log = Logger.getLogger(Server.class);

		OmexServer.callServer(ss);
		
	}
	 
	
	
	public static void callServer(ServerSocket ss) throws IOException {
	       
			
		
	        while(true) {
	        	log.info("waiting for client");
	        	Socket s = null;
	            try 
	            {
	            	
	                s = ss.accept();
	                
	                log.info("A new client is connected : " + s);
	                log.info("Assigning new thread for this client");
	                
	                DataInputStream dis = new DataInputStream(s.getInputStream());
	                String clientMessage=(String)dis.readUTF();
	                if(clientMessage.equals("Exit")) {
	                	s.close();
	                	ss.close();
	                	log.info("Socket closed");
	                	System.exit(0);
	                }
	                else {
	            	String  userID;
	            	String[] stringarray = clientMessage.split("/");
	            	userID=stringarray[0];
	            	String type=stringarray[1];
	            	log.info("type is :"+type);
	            	log.info("USER_ID :"+userID);
	            	
	            	if(type.equals("READ")) {
	            		clientData.add(userID);
	            	}
	            
	            	else if(type.equals("WRITE")) {
	            	log.info(Arrays.asList(map));
	     	        
	            	if(map.containsKey(userID)) {
	            		log.info("user id exist in map");
//	            		Set<Socket> socketArray=map.get(userID);
//	            		socketArray.add(s);
//	            		log.info("after insertion"+socketArray);
//	            		map.put(userID, socketArray);
//	            		log.info(Arrays.asList(map));
	            		Socket socket=map.get(userID);
	            		if(!(socket.isConnected()) && (socket.isClosed())) {
	            			map.put(userID,s);
	            		}
	            		
	            	}
	            	else {
	            		log.info("user id does not exist in map");
//	            		Set<Socket> arr = new HashSet<Socket>();
//	            		arr.add(s);
//	            		log.info(arr);
//	            		map.put(userID, arr);
	            		map.put(userID, s);
	            	}
	            	
	            	}
	     	      
	                Thread t = new ClientHandler(ss,s,dis,userID,map,clientData);
	                t.start(); 
	                
	                Thread.sleep(2000);
	                
	            	}
	                
	            }
	            
	            catch (Exception e){
	                s.close();
	                e.printStackTrace();
	            }
	        }
	  
	    }
}

class ClientHandler extends Thread 
{
	private static Logger log = Logger.getLogger(ClientHandler.class);

	
    final Socket s;
    final ServerSocket ss;
    final String UID;
    final DataInputStream dis;
    final HashMap<String, Socket> mp;
    final Set<String> clientData;
    
    public ClientHandler(ServerSocket ss,Socket s,DataInputStream dis,String UID,HashMap<String,Socket> mp,Set<String> clientData) 
    {
        this.s = s;
        this.UID=UID;
        this.dis=dis;
        this.mp=mp;
        this.ss=ss;
        this.clientData=clientData;
    }
  
    @SuppressWarnings("unused")
	@Override
    public void run() 
    {
//    	GetOMEXMessageResponse omexResponse= new GetOMEXMessageResponse();
//    	Set<String> clientData=GetOMEXMessageResponse.clientData;
    	
    	log.info(clientData);
    	
    	log.info("inside thread");
    	if(clientData.contains(UID) && (mp.containsKey(UID))) {
    		log.info("set and map contain user id "+UID);
            try {
				Socket socket=mp.get(UID);	
				log.info("Socket address of "+UID+" is "+socket);
					
     	        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
     	        dos.writeUTF("Order Placed in "+UID);
     	        log.info("Writing message to client");
				
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
    	else {
    		log.info("Not order placed for this user ID");
    		DataOutputStream dos;
			try {
				dos = new DataOutputStream(s.getOutputStream());
				dos.writeUTF("No data");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 	       
    	}
    }
}
