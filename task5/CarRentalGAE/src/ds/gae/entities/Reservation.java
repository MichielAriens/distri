package ds.gae.entities;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(
			name = "Reservation.getByRenter",
			query = "SELECT r FROM Reservation r WHERE r.carRenter = :name")
})
public class Reservation extends Quote {

	//@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
	//private int id;
	
    private long carId;
    
    /***************
	 * CONSTRUCTOR *
	 ***************/
    
    public Reservation(){
    	super();
    }

    Reservation(Quote quote, long carId) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    			quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }
    
    /******
     * ID *
     ******/
    
    public long getCarId() {
    	return carId;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }
    
    @Override
	public int hashCode() {
		final long prime = 31;
		long result = super.hashCode();
		result = prime * result + carId;
		return (int) result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		Reservation other = (Reservation) obj;
		if (carId != other.carId)
			return false;
		return true;
	}
}