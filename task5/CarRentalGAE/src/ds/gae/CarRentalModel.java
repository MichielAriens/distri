package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Transaction;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.QuoteBatch;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;
 
public class CarRentalModel {
	
	//public Map<String,CarRentalCompany> CRCS = new HashMap<String, CarRentalCompany>();	
	
	private static CarRentalModel instance;

	//private EntityManager em;
	
	
	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}
	
	public CarRentalModel() {
		//em = EMF.get().createEntityManager();
	}
		
	/**
	 * Get the car types available in the given car rental company.
	 *
	 * @param 	crcName
	 * 			the car rental company
	 * @return	The list of car types (i.e. name of car type), available
	 * 			in the given car rental company.
	 */
	public Set<String> getCarTypesNames(String crcName) {
		Set<String> retval = new HashSet<String>();
		for(CarType type : getCarTypesOfCarRentalCompany(crcName)){
			retval.add(type.getName());
		}
		return retval;
	}

    /**
     * Get all registered car rental companies
     *
     * @return	the list of car rental companies
     */
    public Collection<String> getAllRentalCompanyNames() {
    	EntityManager em = EMF.get().createEntityManager();
		try {
			return em.createNamedQuery("CarRentalCompany.getAllNames", String.class).getResultList();
		} finally {
			em.close();
		}
		
    }
	
	/**
	 * Create a quote according to the given reservation constraints (tentative reservation).
	 * 
	 * @param	company
	 * 			name of the car renter company
	 * @param	renterName 
	 * 			name of the car renter 
	 * @param 	constraints
	 * 			reservation constraints for the quote
	 * @return	The newly created quote.
	 *  
	 * @throws ReservationException
	 * 			No car available that fits the given constraints.
	 */
    public Quote createQuote(String company, String renterName, ReservationConstraints constraints) throws ReservationException {
		// FIXME: use persistence instead
    	EntityManager em = EMF.get().createEntityManager();
    	try{
	    	List<CarRentalCompany> results = em.createNamedQuery("CarRentalCompany.getByName", CarRentalCompany.class)
	    			.setParameter("name", company)
	    			.getResultList();
	    	if(results.size() != 1){
	    		throw new ReservationException("In createQuote: " + results.size() + " companies found with that name!");
	    	}
	    	CarRentalCompany crc = results.get(0);
	    			
	    	Quote out = null;
	
	        if (crc != null) {
	            out = crc.createQuote(constraints, renterName);
	        } else {
	        	throw new ReservationException("CarRentalCompany not found.");    	
	        }
	        return out;
    	}finally{
    		em.close();
    	}
    }
    
	/**
	 * Confirm the given quote.
	 *
	 * @param 	q
	 * 			Quote to confirm
	 * 
	 * @throws ReservationException
	 * 			Confirmation of given quote failed.	
	 */
	public void confirmQuote(Quote q) throws ReservationException {
		EntityManager em = EMF.get().createEntityManager();
		try{
			confirmQuote(q,em);
		}finally{
			em.close();
		}
	}
	
	public void confirmQuote(Quote q, EntityManager em) throws ReservationException {
		List<CarRentalCompany> results = em.createNamedQuery("CarRentalCompany.getByName", CarRentalCompany.class)
    			.setParameter("name", q.getRentalCompany())
    			.getResultList();
    	if(results.size() != 1){
    		throw new ReservationException("In createQuote: " + results.size() + " companies found with that name!");
    	}
    	CarRentalCompany crc = results.get(0);
        crc.confirmQuote(q);
	}
	
    /**
	 * Confirm the given list of quotes
	 * 
	 * @param 	quotes 
	 * 			the quotes to confirm
	 * @return	The list of reservations, resulting from confirming all given quotes.
	 * 
	 * @throws 	ReservationException
	 * 			One of the quotes cannot be confirmed. 
	 * 			Therefore none of the given quotes is confirmed.
	 */
    public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {    
    	Map<String, EntityManager> trxMap = new HashMap<>();
    	for(Quote quote : quotes){
    		if(!trxMap.containsKey(quote.getRentalCompany())){
    			EntityManager em = EMF.get().createEntityManager();
    			em.getTransaction().begin();
    			trxMap.put(quote.getRentalCompany(), em);
    		}
    	}
    	boolean rollback = false;
    	try{
    		try{
	    		for(Quote quote : quotes){
	    				confirmQuote(quote, trxMap.get(quote.getRentalCompany()));
	    			}
    		}catch (ReservationException e) {
				rollback = true;
			}
    		if(! rollback){
	    		for(EntityManager em : trxMap.values()){
	    			em.getTransaction().commit();
	    		}
	    		System.out.println("successfull commit");
    		}
    	}finally{
    		if(rollback){
    			System.out.println("Rolling back!");
    			for(EntityManager em : trxMap.values()){
        			em.getTransaction().rollback();
        		}
    		}
    			
			for(EntityManager em : trxMap.values()){
				if(em.getTransaction().isActive())
					em.getTransaction().rollback();
    		}
    		
    		for(EntityManager em : trxMap.values()){
    			em.close();
    		}
    		if(rollback){
    			throw new ReservationException("Reservaton in batch failed");
    		}
    	}
    	
    	
		// TODO add implementation
    	return null;
    }
	
	/**
	 * Get all reservations made by the given car renter.
	 *
	 * @param 	renter
	 * 			name of the car renter
	 * @return	the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return em.createNamedQuery("Reservation.getByRenter", Reservation.class)
					.setParameter("name", renter)
					.getResultList();
		} finally {
			em.close();
		}
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param 	crcName
     * 			the given car rental company
     * @return	The list of car types in the given car rental company.
     */
    public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
    	EntityManager em = EMF.get().createEntityManager();
    	try {
			List<HashMap> results =  em.createNamedQuery("CarRentalCompany.getAllTypesByName", HashMap.class)
					.setParameter("name", crcName)
					.getResultList();
			if(results.size() != 1){
				throw new IllegalArgumentException("getCarTypesOfCarRentalCompany failed");
			}
			HashMap<String, CarType> map = results.get(0);
			return map.values();
			
		} finally {
			em.close();
		}
    }
	
    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A list of car IDs of cars with the given car type.
     */
    public Collection<Long> getCarIdsByCarType(String crcName, CarType carType) {
    	Collection<Long> out = new ArrayList<>();
    	for (Car c : getCarsByCarType(crcName, carType)) {
    		out.add(new Long(c.getId()));
    	}
    	return out;
    }
    
    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A list of car IDs of cars with the given car type.
     */
    public Collection<Integer> getCarDisplayIdsByCarType(String crcName, CarType carType) {
    	Collection<Integer> out = new ArrayList<>();
    	for (Car c : getCarsByCarType(crcName, carType)) {
    		out.add(c.getCid());
    	}
    	System.out.println(out);
    	return out;
    }
    
    /**
     * Get the amount of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A number, representing the amount of cars of the given car type.
     */
    public int getAmountOfCarsByCarType(String crcName, CarType carType) {
    	return this.getCarsByCarType(crcName, carType).size();
    }

	/**
	 * Get the list of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	List of cars of the given car type
	 */
	private List<Car> getCarsByCarType(String crcName, CarType carType) {
		EntityManager em = EMF.get().createEntityManager();
    	try {
			List<HashMap> results =  em.createNamedQuery("CarRentalCompany.getAllTypesByName", HashMap.class)
					.setParameter("name", crcName)
					.getResultList();
			if(results.size() != 1){
				throw new IllegalArgumentException("getCarTypesOfCarRentalCompany failed");
			}
			HashMap<String, CarType> map = results.get(0);
			return new ArrayList<>(map.get(carType.getName()).getCars());
			
		} finally {
			em.close();
		}
	}

	/**
	 * Check whether the given car renter has reservations.
	 *
	 * @param 	renter
	 * 			the car renter
	 * @return	True if the number of reservations of the given car renter is higher than 0.
	 * 			False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;		
	}	
	
	public boolean batchProcessed(long id) throws NotFoundException{
		EntityManager em = EMF.get().createEntityManager();
		try{
			QuoteBatch batch = em.find(QuoteBatch.class, id);
			if(batch == null){
				throw new NotFoundException("Batch not found");
			}
			return batch.isProcessed();
		}finally{
			em.close();
		}
	}
	
	public boolean batchProcessed(String id) throws NumberFormatException, NotFoundException{
		return batchProcessed(Long.valueOf(id));
	}
	
	
	public boolean batchSuccessful(long id) throws NotFoundException{
		EntityManager em = EMF.get().createEntityManager();
		try{
			QuoteBatch batch = em.find(QuoteBatch.class, id);
			if(batch == null){
				throw new NotFoundException("Batch not found");
			}
			return batch.wasSuccessful();
		}finally{
			em.close();
		}
	}
	
	public boolean batchSuccessful(String id) throws NumberFormatException, NotFoundException{
		return batchSuccessful(Long.valueOf(id));
	}
}