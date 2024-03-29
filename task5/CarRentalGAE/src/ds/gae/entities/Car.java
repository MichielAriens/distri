package ds.gae.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Persistent;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;

import ds.gae.EMF;

@Entity
public class Car {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;

	private Integer cid;
	@OneToMany(cascade = CascadeType.ALL)//, mappedBy = "carId")
    private Set<Reservation> reservations = new HashSet<Reservation>();

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(){
    	
    }

    public Car(int i) {
		cid = new Integer(i);
	}

	/******
     * ID *
     ******/
    
    public long getId() {
    	return id.getId();
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
    	EntityManager em = EMF.get().createEntityManager();
    	try{
    		return em.find(CarType.class, id.getParent());
    	}finally{
    		em.close();
    	}
    }

    /****************
     * RESERVATIONS *
     ****************/
    
    public Set<Reservation> getReservations() {
    	return reservations;
    }

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
    
    public int getCid(){
    	return cid;
    }
   
}