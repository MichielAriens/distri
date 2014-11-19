package rental;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;


@Entity
@NamedQueries({
    @NamedQuery(
            name = "Car.getAllIds",
            query = "SELECT e.id FROM Car e"),
    @NamedQuery(
            name = "Car.getReservationsAt",
            query = "SELECT r FROM Reservation r WHERE " +
                    "r.carId = :carId " +
                    "AND( " +
                        "(r.startDate >= :start AND r.startDate <= :end) " +
                        "OR (r.endDate >= :start AND r.endDate <= :end))")
})
public class Car {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    private CarType type;
    
    @OneToMany(cascade = CascadeType.PERSIST)
    private Set<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(){
        
    }
    
    public Car(CarType type){
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }
    
    private Car(int uid, CarType type) {
        this(type);
    	this.id = uid;
    }

    /******
     * ID *
     ******/
    
    public int getId() {
    	return id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
        return type;
    }

    /****************
     * RESERVATIONS *
     ****************/

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

    public Set<Reservation> getReservations() {
        return reservations;
    }
}