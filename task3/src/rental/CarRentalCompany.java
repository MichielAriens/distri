package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarRentalCompany implements ICarRentalCompany{

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
	
	private String name;
	private List<Car> cars;
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();

	/***************
	 * CONSTRUCTOR 
	 * @throws RemoteException *
	 ***************/

	public CarRentalCompany(String name, List<Car> cars) throws RemoteException {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		for(Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
	}

	/********
	 * NAME *
	 ********/

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/*************
	 * CAR TYPES *
	 *************/

	public Collection<CarType> getAllCarTypes() {
		return carTypes.values();
	}
	
	public CarType getCarType(String carTypeName) {
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	public boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName))
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}
	
	/*********
	 * CARS 
	 * @throws RemoteException *
	 *********/
	
	public Car getCar(int uid) throws RemoteException {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}
	
	public List<Car> getAvailableCars(String carType, Date start, Date end) throws RemoteException {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}

	/****************
	 * RESERVATIONS 
	 * @throws RemoteException *
	 ****************/

	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException, RemoteException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}", 
                        new Object[]{name, client, constraints.toString()});
		
		CarType type = getCarType(constraints.getCarType());
		
		if(!isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name
				+ "> No cars available to satisfy the given constraints.");
		
		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());
		
		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	public double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
						/ (1000 * 60 * 60 * 24D));
	}

	
	/**
	 * TODO: Add confirmQuotes(List<Quote>) as synchronized and hide confirmQuote(Quote) from public 
	 * @throws RemoteException 
	 * 
	 */
	private synchronized Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if(availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
	                + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int)(Math.random()*availableCars.size()));
		
		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		return res;
	}

	public synchronized List<Reservation> confirmQuotes(List<Quote> quotes) throws RemoteException, ReservationException {
		List<Reservation> confirmed = new ArrayList<>();
		for(Quote quote: quotes){
			try{
				confirmed.add(confirmQuote(quote));
			}catch (ReservationException | RemoteException e){
				for(Reservation res : confirmed){
					cancelReservation(res);
				}
			}
		}
		return confirmed;
	}

	private void cancelReservation(Reservation res) throws RemoteException {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}  
	

	@Override
	public void cancelReservations(List<Reservation> reservations)
			throws RemoteException {
		for(Reservation res : reservations){
			cancelReservation(res);
		}
	}

	public List<Reservation> getReservationsBy(String clientName) throws RemoteException{
		List<Reservation> requestedReservations = new LinkedList<Reservation>();
		for (Car car : cars) {
			List<Reservation> reservations = car.getReservations();
			for(Reservation reservation: reservations){
				if(reservation.getCarRenter().equals(clientName)){
					requestedReservations.add(reservation);
				}
			}
		}
		return requestedReservations;
	}
	
	public int getNumberOfReservationsForCarType(String carType) throws RemoteException{
		int numberOfRes = 0;
		for (Car car : cars) {
			List<Reservation> reservations = car.getReservations();
			for(Reservation reservation: reservations){
				if(reservation.getCarType().equals(carType)){
					numberOfRes++;
				}
			}
		}
		return numberOfRes;
	}
}