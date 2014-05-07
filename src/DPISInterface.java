import java.rmi.Remote;
import java.rmi.RemoteException;


public interface DPISInterface extends Remote{
	
	public boolean createCRecord(String firstName, String lastName,String description, String status,String station) throws RemoteException;
	public boolean createMRecord(String firstName, String lastName,String address, String lastdate,String lastaddress,String status,String station) throws RemoteException;
	public String getRecordCounts(String station) throws RemoteException;
	public boolean editCRecord (String lastName,String recordID,String newStatus,String station) throws RemoteException;
	
}
