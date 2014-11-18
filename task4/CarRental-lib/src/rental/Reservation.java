package rental;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(
            name = "Reservation.countPerTypeAndCompany",
            query = "SELECT COUNT(r) FROM Reservation r "
                    + "WHERE r.rentalCompany = :company "
                    + "AND r.carType = :type"),
    @NamedQuery(
            name = "Reservation.countPerCustomer",
            query = "SELECT COUNT(r) FROM Reservation r "
                    + "WHERE r.carRenter = :customer"),
    @NamedQuery(
            name = "Reservation.getAllCustomers",
            query = "SELECT DISTINCT r.carRenter FROM Reservation r"),
    @NamedQuery(
            name = "Reservation.getBestCustomer",
            query = "SELECT r.carRenter, COUNT(r.carRenter) FROM Reservation r "
                    + "GROUP BY r.carRenter "
                    + "ORDER BY COUNT(r.carRenter) DESC "),
    @NamedQuery(
            name = "Reservation.getBestType",
            query = "SELECT r.carType FROM Reservation r WHERE r.rentalCompany = :company "
                    + "GROUP BY r.carType "
                    + "ORDER BY COUNT(r.carType) DESC "
                    + "")
})
public class Reservation extends Quote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
   
    
    private int carId;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Reservation(){
        super();
    }

    public Reservation(Quote quote, int carId) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    		quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }
    
    /******
     * ID *
     ******/
    
    public int getCarId() {
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
}