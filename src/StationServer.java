import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.net.*;


public class StationServer extends UnicastRemoteObject implements DPISInterface{
	/**
	 * server class
	 */
	private static final long serialVersionUID = 1L;
	/*Global definition*/
	public static Map<String, String> SPVMRecord = Collections.synchronizedMap(new HashMap<String, String>(1000));
	public static Map<String, String> SPLRecord = Collections.synchronizedMap(new HashMap<String, String>(1000));
	public static Map<String, String> SBPRecord = Collections.synchronizedMap(new HashMap<String, String>(1000));
	int SPLserverPort= 3000;
	int SBPserverPort= 4000;
	int SPVMserverPort= 5000;
	
	/* Super class */
	protected StationServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* Generates the id of CR or MR record*/
	private String generateId(String id){
		int length;
		String number = "";
		length = 5 - id.length();
		for(int i = 0; i < length; i++) {
			number = number + "0";
		}
		number = number + id;
		return number;
	}
	
	/* logs the activities of servers*/
	
	public static void logFile(String fileName, String Operation) throws SecurityException{
		fileName= fileName +"ServerLog.txt";	
		File log = new File(fileName);
		try{
			if(!log.exists()){
		    }
			log.setWritable(true);
		    FileWriter fileWriter = new FileWriter(log, true);

		    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		    bufferedWriter.write(Operation);
		    bufferedWriter.newLine();
		    bufferedWriter.close();
		} catch(IOException e) {
		    System.out.println("COULD NOT LOG!!");
		}
		}
	
	/* Creating the Criminal record */
	public boolean createCRecord(String firstName, String lastName,String description, String status,String station) throws RemoteException {

		Boolean CreateRecordFlag = false;
		String recordId = "";
		
		switch (station) {
			case "SPVM":{ 
				synchronized(SPVMRecord)
				{
					recordId = "CR" + generateId(String.valueOf(SPVMRecord.size()+1));
					SPVMRecord.put(lastName, recordId + " "+ firstName + " " + lastName + " " + description + " " + status);	
					CreateRecordFlag = true; //successful.
					logFile("SPVM","Criminal created");
					//System.out.println(SPVMRecord.values());
				}
			} break;
			case "SPL": { //Longueuil
				synchronized(SPLRecord)
				{
					recordId = "CR" + generateId(String.valueOf(SPVMRecord.size()+1));
					SPLRecord.put(lastName,recordId +" " + firstName + " " + lastName + " " + description + " " + status);
					CreateRecordFlag = true; //successful
					logFile("SPL","Criminal created");
					//System.out.println(SPLRecord.values());
				}
			} break;
			case "SBP": { //Brossard
				synchronized(SBPRecord)
				{
					recordId = "CR" + generateId(String.valueOf(SPVMRecord.size()+1));
					SBPRecord.put(lastName,recordId+" "+ firstName + " " + lastName + " " + description + " " + status);
					CreateRecordFlag = true; //successful
					logFile("SPB","Criminal created");
					//System.out.println(SBPRecord.values());
				}
			} break;

			default:{

			} break;
		}
		return CreateRecordFlag; //return value
	}
	
	
	/* Creating the Missing record */
	public boolean createMRecord(String firstName, String lastName,String address, String lastdate,String lastaddress,String status,String station) throws RemoteException{
		Boolean CreateRecordFlag = false;
		String recordId = "";
		
		switch (station) {
			case "SPVM":{ //Montreal
				synchronized(SPVMRecord)
				{
					recordId = "MR" + generateId(String.valueOf(SPVMRecord.size()+1));
					SPVMRecord.put(lastName, recordId + " "+ firstName + " " + lastName + " " + address + " "+" " +lastdate+ " " +lastaddress +" "+ status);	
					CreateRecordFlag = true; //successful.
					logFile("SPVM","Missing created");
					//System.out.println(SPVMRecord.values());
				}
			} break;
			case "SPL": { //Longueuil
				synchronized(SPLRecord)
				{
					recordId = "MR" + generateId(String.valueOf(SPVMRecord.size()+1));
					SPLRecord.put(lastName, recordId + " "+ firstName + " " + lastName + " " + address + " "+" " +lastdate+ " " +lastaddress +" "+ status);
					CreateRecordFlag = true; //successful
					logFile("SPL","Missing created");
					//System.out.println(SPLRecord.values());
				}
			} break;
			case "SBP": { //Brossard
				synchronized(SBPRecord)
				{
					recordId = "MR" + generateId(String.valueOf(SPVMRecord.size()+1));
					SBPRecord.put(lastName, recordId + " "+ firstName + " " + lastName + " " + address + " "+" " +lastdate+ " " +lastaddress +" "+ status);
					CreateRecordFlag = true; //successful
					logFile("SPB","Missing created");
					//System.out.println(SBPRecord.values());
				}
			} break;

			default:{

			} break;
		}
		return CreateRecordFlag; //return value
		
	}
	
	/* Montreal Police UDP server */
	public class SPVMUdpServer implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			DatagramSocket aSocket = null;
			try{
				aSocket = new DatagramSocket(SPVMserverPort);
				byte[] buffer = new byte[10];
				while(true){
					DatagramPacket request = new DatagramPacket(buffer,buffer.length);
					aSocket.receive(request);
					String message = "SPVM: "+String.valueOf(SPVMRecord.size());
					buffer = message.getBytes();
					DatagramPacket reply = new DatagramPacket(buffer,buffer.length,request.getAddress(),request.getPort());
					aSocket.send(reply);
				}
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
			}catch (IOException e) {System.out.println("IO: " + e.getMessage());
			}finally {if(aSocket != null) aSocket.close();}
		}
		
	}

	/* Longueuil Police UDP server */
	public class SPLUdpServer implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			DatagramSocket aSocket = null;
			try{
				aSocket = new DatagramSocket(SPLserverPort);
				byte[] buffer = new byte[10];
				while(true){
					DatagramPacket request = new DatagramPacket(buffer,buffer.length);
					aSocket.receive(request);
					String message = "SPL: "+String.valueOf(SPLRecord.size());
					buffer = message.getBytes();
					DatagramPacket reply = new DatagramPacket(buffer,buffer.length,request.getAddress(),request.getPort());
					aSocket.send(reply);
					System.out.println(message);
				}
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
			}catch (IOException e) {System.out.println("IO: " + e.getMessage());
			}finally {if(aSocket != null) aSocket.close();}
		}
		
	}
	
	/* Brossard Police UDP server */
	public class SBPUdpServer implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			DatagramSocket aSocket = null;
			try{
				aSocket = new DatagramSocket(SBPserverPort);
				byte[] buffer = new byte[10];
				while(true){
					DatagramPacket request = new DatagramPacket(buffer,buffer.length);
					aSocket.receive(request);
					String message = "SBP: "+String.valueOf(SBPRecord.size());
					buffer = message.getBytes();
					DatagramPacket reply = new DatagramPacket(buffer,buffer.length,request.getAddress(),request.getPort());
					aSocket.send(reply);
				}
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
			}catch (IOException e) {System.out.println("IO: " + e.getMessage());
			}finally {if(aSocket != null) aSocket.close();}
		}
		
	}

	
	/* record counting */
	@SuppressWarnings("deprecation")
	public String getRecordCounts(String station) throws RemoteException{
		DatagramSocket DPISSocket = null;
		DatagramSocket DPISSocket1 = null;
		DatagramSocket DPISSocket2 = null;
		Thread t1 = new Thread(new SPLUdpServer());
		Thread t2 = new Thread(new SBPUdpServer());
		Thread t3 = new Thread(new SPVMUdpServer());
		SPVMserverPort = SPVMserverPort +2;
		SPLserverPort = SPLserverPort +2; 
		SBPserverPort = SBPserverPort +2;
		
		t1.start();
		t2.start();
		t3.start();
		

		
		String message = null;
		byte[] buffer = new byte[10];
		
		
		switch(station){
		case "SPVM":{
			try {
				message = null;
				
				message += "SPVM " + String.valueOf(SPVMRecord.size());
				
				DPISSocket = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName("localhost");
				byte[]m = "send count".getBytes();
				DatagramPacket request =new DatagramPacket (m,"send count".length(),aHost,SPLserverPort);
				DPISSocket.send(request);
				DatagramPacket reply =new DatagramPacket (buffer,buffer.length);
				DPISSocket.receive(reply);
				message += new String(reply.getData());
				
				DatagramPacket request1 =new DatagramPacket (m,"send count".length(),aHost,SBPserverPort);
				DPISSocket.send(request1);
				DatagramPacket reply1 =new DatagramPacket (buffer,buffer.length);
				DPISSocket.receive(reply1);
				message += new String(reply1.getData());
				
				
				
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
			}catch (IOException e) {System.out.println("IO: " + e.getMessage());
			}finally {if(DPISSocket != null) DPISSocket.close();}
		}break;
		case "SBP":{
			message = null;
			try {
				DPISSocket1 = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName("localhost");
				byte[]m = "send count".getBytes();
				DatagramPacket request =new DatagramPacket (m,"send count".length(),aHost,SPVMserverPort);
				DPISSocket1.send(request);
				DatagramPacket reply =new DatagramPacket (buffer,buffer.length);
				DPISSocket1.receive(reply);
				message = new String(reply.getData());
				
				message += "SBP " + String.valueOf(SBPRecord.size()) +" ";
				
				DatagramPacket request1 =new DatagramPacket (m,"send count".length(),aHost,SPLserverPort);
				DPISSocket1.send(request1);
				DatagramPacket reply1 =new DatagramPacket (buffer,buffer.length);
				DPISSocket1.receive(reply1);
				message += new String(reply1.getData());
				
				
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
			}catch (IOException e) {System.out.println("IO: " + e.getMessage());
			}finally {if(DPISSocket1 != null) DPISSocket1.close();}

		}break;
		case "SPL":{
			message = null;
			try {
				DPISSocket2 = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName("localhost");
				byte[]m = "send count".getBytes();
				DatagramPacket request =new DatagramPacket (m,"send count".length(),aHost,SPVMserverPort);
				DPISSocket2.send(request);
				DatagramPacket reply =new DatagramPacket (buffer,buffer.length);
				DPISSocket2.receive(reply);
				message = new String(reply.getData());
				
				DatagramPacket request1 =new DatagramPacket (m,"send count".length(),aHost,SBPserverPort);
				DPISSocket2.send(request1);
				DatagramPacket reply1 =new DatagramPacket (buffer,buffer.length);
				DPISSocket2.receive(reply1);
				message += new String(reply1.getData());
				message += "SPL " + String.valueOf(SPLRecord.size());
				
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
			}catch (IOException e) {System.out.println("IO: " + e.getMessage());
			}finally {if(DPISSocket2 != null) DPISSocket2.close();}
		}break;
		default:{

		} break;
		}
		message = "SPVM:" +String.valueOf(SPVMRecord.size())+ " " + "SPL:"+String.valueOf(SPLRecord.size())+" "+"SBP: "+String.valueOf(SBPRecord.size());
		return message;
		
	}
	
	/* editing criminal recor */
	public boolean editCRecord (String lastName,String recordId,String newStatus, String station) throws RemoteException{
		Boolean GetRecordFlag = false;
		
		switch (station) {
			case "SPVM":{ //Montreal
				synchronized(SPVMRecord)
				{
					if(SPVMRecord.containsKey(lastName)) {
						StringTokenizer tokens = new StringTokenizer(SPVMRecord.get(lastName), " ");
						int i = 0;
						int length = tokens.countTokens() - 1;
						String mod_rec = "";
						while (tokens.hasMoreElements()) {
							if(i == length) {
								mod_rec = mod_rec + newStatus;
							} else {
								mod_rec = mod_rec + tokens.nextElement() + " ";
							}
							i++;						   
						}
						SPVMRecord.remove(lastName);
						SPVMRecord.put(lastName, mod_rec.substring(0, mod_rec.length() -1 ));
						//System.out.println(SPVMRecord.values());
						GetRecordFlag = true; //successful.
						logFile("SPVM","Record Edited");
					} else {
						GetRecordFlag = false;
					}
				}
			} break;
			case "SPL": { //Longueuil
				synchronized(SPLRecord)
				{
					if(SPLRecord.containsKey(lastName)) {
						StringTokenizer tokens = new StringTokenizer(SPLRecord.get(lastName), " ");
						int i = 0;
						int lngth = tokens.countTokens() - 1;
						String mod_rec = "";
						while (tokens.hasMoreElements()) {
							if(i == lngth) {
								mod_rec = mod_rec + newStatus;
							} else {
								mod_rec = mod_rec + tokens.nextElement() + " ";
							}
							i++;						   
						}
						SPLRecord.remove(lastName);
						SPLRecord.put(recordId, mod_rec.substring(0, mod_rec.length() -1 ));
						//System.out.println(SPLRecord.values());
						GetRecordFlag = true; //successful.
						logFile("SPL","Record Edited");
					} else {
						GetRecordFlag = false;
					}
				}
			} break;
			case "SPB": { //Brossard
				synchronized(SBPRecord)
				{
					if(SBPRecord.containsKey(lastName)) {
						StringTokenizer tokens = new StringTokenizer(SBPRecord.get(lastName), " ");
						int i = 0;
						int lngth = tokens.countTokens() - 1;
						String mod_rec = "";
						while (tokens.hasMoreElements()) {
							if(i == lngth) {
								mod_rec = mod_rec + newStatus;
							} else {
								mod_rec = mod_rec + tokens.nextElement() + " ";
							}
							i++;						   
						}
						SBPRecord.remove(lastName);
						SBPRecord.put(recordId, mod_rec.substring(0, mod_rec.length() -1 ));
						//System.out.println(SBPRecord.values());
						logFile("SBP","Record Edited");
						GetRecordFlag = true; //successful.
					} else {
						GetRecordFlag = false;
					}
				}
			} break;

			default:{

			} break;
		}
		return GetRecordFlag; //return value
	
	}
	/**
	 * @param args
	 */
	
	/* main */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Registry r = LocateRegistry.createRegistry(1987);
			r.rebind("SPVM",  new StationServer());
			System.out.println("Montreal Police Server is Ready.");			
			r.rebind("SPL",  new StationServer());
			System.out.println("Longueuil Police Server is Ready.");			
			r.rebind("SBP",  new StationServer());
			System.out.println("Brossard Police Server is Ready.");
		}
		catch(Exception e){
			System.out.println("Exception in servers Startup:"+e);
		}
	}

}
