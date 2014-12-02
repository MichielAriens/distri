package ds.gae.entities;

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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import ds.gae.EMF;
import ds.gae.ReservationException;

@Entity
@NamedQueries({
	@NamedQuery(
			name = "CarRentalCompany.getByName",
			query = "SELECT c FROM CarRentalCompany c WHERE c.name = :name"),
	@NamedQuery(
			name = "CarRentalCompany.getAllNames",
			query = "SELECT c.name FROM CarRentalCompany c"),
	@NamedQuery(
			name = "CarRentalCompany.getAllTypesByName",
			query = "SELECT c.carTypes FROM CarRentalCompany c WHERE c.name = :name"),
	@NamedQuery(
			name = "CarRentalCompany.getAllTypeNamesByName",
			query = "SELECT c.carTypes.name FROM CarRentalCompany c WHERE c.name = :name"),
	@NamedQuery(
			name = "CarRentalCompany.getCarsByCarType",
			query = "SELECT c FROM Car c, CarRentalCompany crc WHERE crc.name = :name AND c IN crc.carTypes.cars AND c.type = :carType")

})
public class CarRentalCompany {

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	//private long id;
	private String name;
	@OneToMany(cascade = CascadeType.ALL)
	@MapKey(name = "typeName")
	private Map<String, CarType> carTypes = new HashMap<>();

	/***************
	 * CONSTRUCTOR *
	 ***************/
	
	public CarRentalCompany(){
	}

	public CarRentalCompany(String name, Set<CarType> types) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		for(CarType ct : types){
			carTypes.put(ct.getName(), ct);
		}
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
		//FIXME change to query.
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	public boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName))
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : getCars()) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}
	
	/*********
	 * CARS *
	 *********/
	
	private Car getCar(long uid) {
		EntityManager em = EMF.get().createEntityManager();
		try{
			Car car = em.find(Car.class, uid);
			if (car == null){
				throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
			}
			return car;
		}finally{
			em.close();
		}
	}
	
	public Set<Car> getCars() {
    	Set<Car> retval = new HashSet<Car>();
    	for(CarType ct : this.getAllCarTypes()){
    		retval.addAll(ct.getCars());
    	}
    	return retval;
    }
	
	private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : getCars()) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}

	/****************
	 * RESERVATIONS *
	 ****************/

	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
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
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
						/ (1000 * 60 * 60 * 24D));
	}

	public Reservation confirmQuote(Quote quote) throws ReservationException {
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

	public void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}
}