package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import rental.CarRentalSession;
import rental.CarType;
import rental.ReservationConstraints;
import rental.ReservationException;

public class RenterTestClient extends BasicClient{
	
	public static void main(String[] args){
		try{
			RenterTestClient client = new RenterTestClient();
			client.run();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public RenterTestClient() throws RemoteException, NotBoundException {
		super();
	}

	private void run() throws RemoteException, ReservationException {
		CarRentalSession session = new CarRentalSession(server);
		Date start = new Date(2014,1,1); Date end = new Date(2014,1,2);
		List<CarType> typesA = session.getAvailablaCarTypes(start, end, "hertz");
		List<CarType> typesB = session.getAvailablaCarTypes(start, end, "dockx");
		System.out.println("HERTZ: " + typesA);
		ReservationConstraints cons = new ReservationConstraints(start, end, "Premium");
		session.createQuote(cons, "hertz");
		System.out.println("DOCKX: " + typesB);
		cons = new ReservationConstraints(start, end, "Eco");
		session.createQuote(cons, "dockx");
		session.confirmQuotes();
	}
	
	
	

}
