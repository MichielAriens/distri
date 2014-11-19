package rental;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(
            name = "CarType.getByName",
            query = "SELECT e FROM CarType e WHERE e.name = :name"),
    @NamedQuery(
            name = "CarType.getAvailable",
            query = "SELECT DISTINCT e.type FROM Car e WHERE " +
                        "NOT EXISTS ( " +
                            "SELECT r.carType FROM Reservation r WHERE " +
                            "r.carId = e.id " +
                            "AND( " +
                                "(r.startDate >= :start AND r.startDate <= :end) " +
                                "OR (r.endDate >= :start AND r.endDate <= :end))"
                    + " ) ")
    
    
    /**
    @NamedQuery(
            name = "CarType.getAvailable",
            query = "SELECT e FROM CarType e WHERE "
                    + "NOT EXISTS ("
                    + "             SELECT r FROM Reservation r WHERE"
                    + "             r.carType = e.name "
                    + "             AND ("
                    + "             (r.startDate >= :start AND r.startDate <= :end) "
                    + "             OR (r.endDate >= :start AND r.endDate <= :end)))")
                    * */
})
public class CarType implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String name;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    //trunk space in liters
    private float trunkSpace;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public CarType(){
        
    }
    
    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }

    public String getName() {
    	return name;
    }
    
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    public float getTrunkSpace() {
    	return trunkSpace;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(), getTrunkSpace());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
	if (obj == null)
            return false;
	if (getClass() != obj.getClass())
            return false;
	CarType other = (CarType) obj;
	if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
	return true;
    }
}