import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Scanner;


public class OfficerClient  {

	
	/**
	 * @param args
	 */
	/* displays menu */
	
	public static void showMenu()
	{
		System.out.println("Please select an option (1-5)");
		System.out.println("1. Create a new Criminal Record.");
		System.out.println("2. Create a new Missing Record.");
		System.out.println("3. Edit an existing Criminal Record.");
		System.out.println("4. View Total Records.");
		System.out.println("5. Exit");
	}
	/* gets station from id*/
	public static String getStation(String id){
		String station;
		int length;
		length = id.length() - 4;
		station = id.substring(0, length);
		return station;
	}
	/* logs activity of client */
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
	
	public static void main(String[] args) {
		
		
		try {
			System.setSecurityManager(new RMISecurityManager());
			DPISInterface SPVMserver = (DPISInterface) Naming.lookup("rmi://localhost:1987/SPVM");
			DPISInterface SPLserver = (DPISInterface) Naming.lookup("rmi://localhost:1987/SPL");
			DPISInterface SBPserver = (DPISInterface) Naming.lookup("rmi://localhost:1987/SBP");
			
			String badgeId,lastName,firstName,description, status,station,address,lastDate,lastLocation,recordId,message = null;
			int choice=0;
			boolean result;
			Scanner input = new Scanner(System.in);
			System.out.println("\n****Welcome to Police Information System****\n");
			System.out.println("Enter Your Badge ID:");
			badgeId = input.next();
			station = getStation(badgeId);
			
			showMenu();
			while(true)
			{
				Boolean valid = false;
				result = false;
				while(!valid)
				{
					try{
						choice=input.nextInt();
						valid=true;
					}
					catch(Exception e)
					{
						System.out.println("Invalid Input, please enter an Integer");
						valid=false;
						input.nextLine();
					}
				}
					
				switch(choice)
				{
				case 1:
					System.out.println("Enter First name");
					firstName= input.next();
					System.out.println("Enter Last name");
					lastName = input.next();
					System.out.println("Enter Description");
					description = input.next();
					System.out.println("Enter Status");
					status = input.next();
					
					if(station.equals("SPVM"))
						result=SPVMserver.createCRecord(firstName, lastName, description, status,station);
					if(station.equals("SPL"))
						result=SPLserver.createCRecord(firstName, lastName, description, status, station);
					if(station.equals("SBP"))
						result=SBPserver.createCRecord(firstName, lastName, description, status, station);
					if(result == true)
						logFile(badgeId,"Created criminal successful");
					else
						logFile(badgeId,"Created criminal unsuccessful");
					showMenu();
					break;
				case 2:
					System.out.println("Enter First name");
					firstName= input.next();
					System.out.println("Enter Last name");
					lastName = input.next();
					System.out.println("Enter address");
					address = input.next();
					System.out.println("Enter last date seen");
					lastDate = input.next();
					System.out.println("Enter Last Location");
					lastLocation = input.next();
					System.out.println("Enter Status");
					status = input.next();
					if(station.equals("SPVM"))
						result=SPVMserver.createMRecord(firstName, lastName, address, lastDate, lastLocation, status, station);
					if(station.equals("SPL"))
						result=SPLserver.createMRecord(firstName, lastName, address, lastDate, lastLocation, status, station);
					if(station.equals("SBP"))
						result=SBPserver.createMRecord(firstName, lastName, address, lastDate, lastLocation, status, station);
					if(result == true)
						logFile(badgeId,"Created missing successful");
					else
						logFile(badgeId,"Created missing unsuccessful");
					showMenu();
					break;
				case 3:
					System.out.println("Enter Last name");
					lastName = input.next();
					System.out.println("Enter Record ID");
					recordId = input.next();
					System.out.println("Enter Status");
					status = input.next();
					if(station.equals("SPVM"))
						result=SPVMserver.editCRecord(lastName, recordId, status, station);
					if(station.equals("SPL"))
						result=SPLserver.editCRecord(lastName, recordId, status, station);
					if(station.equals("SBP"))
						result=SBPserver.editCRecord(lastName, recordId, status, station);
					if(result == true)
						logFile(badgeId,"Edit criminal successful");
					else
						logFile(badgeId,"Edit criminal unsuccessful");
					showMenu();
					break;
				case 4:
					if(station.equals("SPVM")){
						message=SPVMserver.getRecordCounts(station);
						System.out.println(message);}
					if(station.equals("SPL")){
						message=SPLserver.getRecordCounts(station);
						System.out.println(message);}
					if(station.equals("SBP")){
						message=SBPserver.getRecordCounts(station);
					System.out.println(message);}
					logFile(badgeId,"viewd record successful");
					showMenu();
					break;
				case 5:
					
					System.exit(0);
				default:
					System.out.println("Invalid Input, please try again.");
				}
			}
			
		}catch (Exception e) {
		e.printStackTrace();
		}
	}

}
