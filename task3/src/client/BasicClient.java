package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rental.IRentalServer;

/**
 * Provides implementing classes with a RentalServer and a registry.
 * @author michiel
 *
 */
public class BasicClient {

	protected Registry registry;
	protected IRentalServer server; 
	
	
	public BasicClient() throws RemoteException, NotBoundException {
		this.registry = LocateRegistry.getRegistry("localhost", 1099);
		this.server = (IRentalServer) registry.lookup("rentalServer");
	}
}
