import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Scanner;



public class Clienttest2 implements Runnable {
	
	String firstName = "";
	String lastName = "";
	String description = "";
	String address = "";
	String lastDate = "";	
	String lastLocation = "";
	String status = ""; 
	String Id = "";
	String recordID = "";
	String operation = "";
	String station = "";
	String message ="";
	
	/*interface*/
	
	static DPISInterface SPVMserver;
	static DPISInterface SPLserver;
	static DPISInterface SBPserver;
	
	/* gets station from id*/
	public static String getStation(String id){
		String station;
		int length;
		length = id.length() - 4;
		station = id.substring(0, length);
		return station;
	}
	/*logs each clients activity*/
	
	public static void logFile(String fileName, String Operation) throws SecurityException{
		fileName= fileName +"Log.txt";	
		File log = new File(fileName);
		try{
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
	
	/* test client */
	
	public Clienttest2(String ID,String firstName, String lastName, String description, String address, String lastDate,
			String lastLocation, String status, String recordID, String operation) {
		this.Id = ID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = description;
		this.address = address;
		this.lastDate = lastDate;	
		this.lastLocation = lastLocation;
		this.status = status; 
		this.recordID = recordID;
		this.operation = operation;
	}

	public void run() {
		try {
		    System.setSecurityManager(new RMISecurityManager());
		    DPISInterface SPVMserver = (DPISInterface) Naming.lookup("rmi://localhost:1987/SPVM");
			DPISInterface SPLserver = (DPISInterface) Naming.lookup("rmi://localhost:1987/SPL");
			DPISInterface SBPserver = (DPISInterface) Naming.lookup("rmi://localhost:1987/SBP");
			Clienttest2.SPVMserver = SPVMserver;
		    Clienttest2.SPLserver=SPLserver;
		    Clienttest2.SBPserver=SBPserver;
		    
	    } catch (Exception e) {
	    	System.out.println("Servers Binding Exception: "+ e);
	    }
		
		station = getStation(Id);
		
		switch(operation) {
			case "CR": {
				//System.out.println("CRecord Operation");
				Boolean result = false;
				try {
					if(station.equals("SPVM"))
						result=SPVMserver.createCRecord(firstName, lastName, description, status, station);
					if(station.equals("SPL"))
						result=SPLserver.createCRecord(firstName, lastName, description, status, station);
					if(station.equals("SBP"))
						result=SBPserver.createCRecord(firstName, lastName, description, status, station);
					if(result){
						System.out.println("Criminal created");
						logFile(Id, "Criminal created");
					}
					else
					{
						System.out.println("Criminal not created");
						logFile(Id, "Criminal not created");
					}
				} catch(Exception ex) {
					System.out.println("Criminal Exception: "+ ex);
				}
			} break;
			case "MR": {
				//System.out.println("MRecord Operation");
				Boolean result = false;
				try {
					if(station.equals("SPVM"))
						result=SPVMserver.createMRecord(firstName, lastName, address, lastDate, lastLocation, status, station);
					if(station.equals("SPL"))
						result=SPLserver.createMRecord(firstName, lastName, address, lastDate, lastLocation, status, station);
					if(station.equals("SBP"))
						result=SBPserver.createMRecord(firstName, lastName, address, lastDate, lastLocation, status, station);
					if(result == true) {
						System.out.println("Missing Created");
						logFile(Id, "Missing created");
					} else {
						System.out.println("Missing Not Created Due to Unexpected Error");
						logFile(Id, "Missing not created");
					}
				} catch(Exception ex) {
					System.out.println("Missing Exception: "+ ex);
				}
			} break;
			case "EDT": {
				//System.out.println("Edit Operation");
				Boolean result = false;				
				try {
					if(station.equals("SPVM"))
						result=SPVMserver.editCRecord(lastName, recordID, status, station);
					if(station.equals("SPL"))
						result=SPLserver.editCRecord(lastName, recordID, status, station);
					if(station.equals("SBP"))
						result=SBPserver.editCRecord(lastName, recordID, status, station);
					if(result == true) {
						logFile(Id, "Criminal edit successful");
						System.out.println("Edit Succesful");
					} else {
						logFile(Id, "Edit unsuccessful");
						//System.out.println("Not Found");
					}
				} catch(Exception ex) {
					System.out.println("Edit Exception: "+ ex);
				}
			} break;
			case "viw":{
				try {
					if(station.equals("SPVM"))
						message = SPVMserver.getRecordCounts(station);
					if(station.equals("SPL"))
						message = SPLserver.getRecordCounts(station);
					if(station.equals("SBP"))
						message = SBPserver.getRecordCounts(station);
					
					logFile(Id, "record viewed");
					System.out.println("record viewed Succesful \n"+ message);
					
				} catch(Exception ex) {
					System.out.println("Edit Exception: "+ ex);
				}
			} break;
			default:
				break;
		}
		
	}
	
	public static void main(String[] args) {
		//Criminal Record threads
		Thread client1 = new Thread(new Clienttest2("SPVM1001","fname1","lname1", "description1", "", "", "", "status1", "", "CR"));
		Thread client2 = new Thread(new Clienttest2("SPL1001","fname2","lname2", "description2", "", "", "", "status2", "", "CR"));
		Thread client3 = new Thread(new Clienttest2("SBP1002","fname3","lname3", "description3", "", "", "", "status3", "", "CR"));
		Thread client4 = new Thread(new Clienttest2("SPVM1010","fname4","lname4", "description4", "", "", "", "status4", "", "CR"));
		Thread client5 = new Thread(new Clienttest2("SPL1099","fname5","lname5", "description5", "", "", "", "status5", "", "CR"));
		
		//Missing Record threads
		Thread client6 = new Thread(new Clienttest2("SPL2001","fname6","lname6","", "address6", "lastdate6", "lastlocation6", "status6", "", "MR"));
		Thread client7 = new Thread(new Clienttest2("SPVM2001","fname7","lname7", "", "address7", "lastdate7", "lastlocation7", "status7",  "", "MR"));
		Thread client8 = new Thread(new Clienttest2("SBP1002","fname8","lname8", "", "address8", "lastdate8", "lastlocation8", "status8",  "", "MR"));
		Thread client9 = new Thread(new Clienttest2("SPVM2003","fname9","lname9", "", "address9", "lastdate9", "lastlocation9", "status9",  "", "MR"));
		Thread client10 = new Thread(new Clienttest2("SPL2003","fname10","lname10", "", "address10", "lastdate10", "lastlocation10", "status10",  "", "MR"));
		
		//Edit Record threads
		Thread client11 = new Thread(new Clienttest2("SPVM3001","","lname1", "", "", "", "", "status202", "CR00001", "EDT"));
		Thread client12 = new Thread(new Clienttest2("SPL3004","","lname2", "", "", "", "", "status202",  "CR00001", "EDT"));
		Thread client13 = new Thread(new Clienttest2("SBP3006","","lname5", "", "", "", "", "status202",  "CR00002", "EDT"));
		Thread client14 = new Thread(new Clienttest2("SPVM3005","","lname3", "", "", "", "", "status202",  "CR00001", "EDT"));
		Thread client15 = new Thread(new Clienttest2("SPL3005","","lname4", "", "", "", "", "status202",  "CR00002", "EDT"));
		
		//Record Count threads
		Thread client16 = new Thread(new Clienttest2("SPVM3001","","", "", "", "", "", "", "", "viw"));
		Thread client17 = new Thread(new Clienttest2("SPL3004","","", "", "", "", "", "",  "", "viw"));
		
		client1.start();
		client2.start();
		client3.start();
		client4.start();
		client5.start();
		client6.start();
		client7.start();
		client8.start();
		client9.start();
		client10.start();
		client11.start();
		client12.start();
		client13.start();
		client14.start();
		client15.start();
		client16.start();
		client17.start();
	}
}
