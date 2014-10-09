package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;

import rental.CarType;
import rental.ICar;
import rental.ICarRentalCompany;
import rental.Quote;
import rental.Reservation;

public class Client extends AbstractScriptedSimpleTest {
	
	public static String carRentalCompanyName = "Hertz";
	
	Registry registry;
	ICarRentalCompany crc;
	
	
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
	
	/**
	 * Check which car types are available in the given period
	 * and print this list of car types.
	 *
	 * @param 	start
	 * 			start time of the period
	 * @param 	end
	 * 			end time of the period
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
		for (CarType ct  : crc.getAvailableCarTypes(start, end)){
			System.out.println(ct.getName());
		}
	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param	clientName 
	 * 			name of the client 
	 * @param 	start 
	 * 			start time for the quote
	 * @param 	end 
	 * 			end time for the quote
	 * @param 	carType 
	 * 			type of car to be reserved
	 * @return	the newly created quote
	 *  
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end,
			String carType) throws Exception {
		double dayprice = crc.getCarType(carType).getRentalPricePerDay();
		double price = crc.calculateRentalPrice(dayprice, start, end);
		return new Quote(clientName, start, end, carRentalCompanyName, carType, price );
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param 	quote 
	 * 			the quote to be confirmed
	 * @return	the final reservation of a car
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		try{
			ICar car = crc.getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate()).get(0);
			Reservation reservation = new Reservation(quote, car.getId());
			car.addReservation(reservation);
			return reservation;
		}catch (IndexOutOfBoundsException e){
			throw new Exception(String.format("No cars available for %s", quote.getCarRenter()));
		}
	}
	
	/**
	 * Get all reservations made by the given client.
	 *
	 * @param 	clientName
	 * 			name of the client
	 * @return	the list of reservations of the given client
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsBy(String clientName) throws Exception {
		return crc.getReservationsBy(clientName);
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param 	carType 
	 * 			name of the car type
	 * @return 	number of reservations for the given car type
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException("TODO");
		return 0;
	}
}