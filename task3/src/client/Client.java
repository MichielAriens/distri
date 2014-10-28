package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

import rental.CarRentalSession;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.IRentalServer;
import rental.ManagerSession;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public class Client extends AbstractScriptedTripTest<CarRentalSession, ManagerSession> {
	
	public static String carRentalCompanyName = "Hertz";
	
	Registry registry;
	ICarRentalCompany crc;
	private IRentalServer server; 
	
	
	/********
	 * MAIN *
	 ********/
	
	public static void main(String[] args) throws Exception {		
		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName);
		client.run();
	}
	
	/***************
	 * CONSTRUCTOR *
	 ***************/
	
	public Client(String scriptFile, String carRentalCompanyName) {
		super(scriptFile);
		try {
			this.server = (IRentalServer) registry.lookup("rentalServer");
			this.registry = LocateRegistry.getRegistry("localhost", 1099);
			this.crc = (ICarRentalCompany) registry.lookup(carRentalCompanyName);			
		} catch (RemoteException e) {
			//e.printStackTrace();
			throw new RuntimeException("Could not connect to registry!");
		} catch (NotBoundException e) {
			//e.printStackTrace();
			throw new RuntimeException("The car rental company specified was not found on the registry.");
		}
	}

	@Override
	protected CarRentalSession getNewReservationSession(String name)
			throws Exception {
		CarRentalSession session = new CarRentalSession(server);
		session.setClientName(name);
		return session;
	}

	@Override
	protected ManagerSession getNewManagerSession(String name) throws Exception {
		ManagerSession session = new ManagerSession(server);
		return session;
	}

	@Override
	protected void checkForAvailableCarTypes(CarRentalSession session,
			Date start, Date end) throws Exception {
		System.out.println(session.getAvailablaCarTypes(start, end));
		
	}

	@Override
	protected String getCheapestCarType(CarRentalSession session, Date start,
			Date end) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addQuoteToSession(CarRentalSession session, Date start,
			Date end, String carType, String carRentalName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<Reservation> confirmQuotes(CarRentalSession session)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getNumberOfReservationsBy(ManagerSession ms, String clientName)
			throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Set<String> getBestClients(ManagerSession ms) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getNumberOfReservationsForCarType(ManagerSession ms,
			String carRentalCompanyName, String carType) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected CarType getMostPopularCarTypeIn(ManagerSession ms,
			String carRentalCompanyName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}