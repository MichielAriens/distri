package rental;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Sets up the server.
 * @author Michiel, Julie
 * 
 * TODO: Protect methods in car rental company that can bring the system into a bad state when called by client. 
 *
 */
public class StartServer {
	public static void main(String[] args){
		try{
			IRentalServer server = new RentalServer();
			
			
			IRentalServer stub = (IRentalServer) UnicastRemoteObject.exportObject(server,0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("rentalServer", stub);
			System.out.println("Rental server bound");
			
			
			
		} catch (RemoteException e) {
			System.out.println("Remote exception on startup. Is the RMIRegistry running?");
			e.printStackTrace();
		}finally{
			
		}
		
		
	}
}
