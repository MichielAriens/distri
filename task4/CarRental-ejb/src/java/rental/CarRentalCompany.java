package rental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;

@Entity
@NamedQueries({
    @NamedQuery(
            name = "CarRentalCompany.findAll",
            query = "SELECT c FROM CarRentalCompany c"),
    @NamedQuery(
            name = "CarRentalCompany.findAllNames",
            query = "SELECT c.name FROM CarRentalCompany c"),
    @NamedQuery(
            name = "CarRentalCompany.findAllCarTypes",
            query = "SELECT c.carTypes FROM CarRentalCompany c"),
    @NamedQuery(
            name = "CarRentalCompany.getByName",
            query = "SELECT e FROM CarRentalCompany e WHERE e.name = :compName")
        
})
public class CarRentalCompany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
    private String name;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Car> cars;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<CarType> carTypes = new HashSet<CarType>();

    /***************
     * CONSTRUCTOR *
     ***************/
    public CarRentalCompany(){
        
    }
    
    
    public CarRentalCompany(String name, List<Car> cars) {
        logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
        setName(name);
        this.cars = cars;
        for (Car car : cars) {
            carTypes.add(car.getType());
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
    
    public Collection<CarType> getAllTypes() {
        return carTypes;
    }

    public CarType getType(String carTypeName) {
        for(CarType type:carTypes){
            if(type.getName().equals(carTypeName))
                return type;
        }
        throw new IllegalArgumentException("<" + carTypeName + "> No cartype of name " + carTypeName);
    }


    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
        return getAvailableCarTypes(start, end).contains(getType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/
    
    public Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    public Set<Car> getCars(CarType type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (car.getType().equals(type)) {
                out.add(car);
            }
        }
        return out;
    }
    
     public Set<Car> getCars(String type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (type.equals(car.getType().getName())) {
                out.add(car);
            }
        }
        return out;
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<Car>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /****************
     * RESERVATIONS *
     ****************/
    
    public Quote createQuote(ReservationConstraints constraints, String guest)
            throws ReservationException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[]{name, guest, constraints.toString()});

        CarType type = getType(constraints.getCarType());

        if (!isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");
        }

        double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(), constraints.getEndDate());

        return new Quote(guest, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24D));
    }

    public Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty()) {
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        }
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
        getCar(res.getCarId()).removeReservation(res);
    }
    
    public Set<Reservation> getReservationsBy(String renter) {
        logger.log(Level.INFO, "<{0}> Retrieving reservations by {1}", new Object[]{name, renter});
        Set<Reservation> out = new HashSet<Reservation>();
        for(Car c : cars) {
            for(Reservation r : c.getReservations()) {
                if(r.getCarRenter().equals(renter))
                    out.add(r);
            }
        }
        return out;
    }

    @Deprecated
    public CarType getMostPopularCarType() {
        CarType best = null;
        for (CarType carType : getAllCarTypes()) {
            if (best == null) {
                best = carType;
            }
            if (getNumberOfReservationsForCarType(carType.getName())
                    > getNumberOfReservationsForCarType(best.getName())) {
                best = carType;
            }
        }
        return best;
    }

    public Collection<CarType> getAllCarTypes() {
        return carTypes;
    }
    
    @Deprecated
    public int getNumberOfReservationsForCarType(String carType) {
        int numberOfRes = 0;
        for (Car car : cars) {
            Set<Reservation> reservations = car.getReservations();
            for (Reservation reservation : reservations) {
                if (reservation.getCarType().equals(carType)) {
                    numberOfRes++;
                }
            }
        }
        return numberOfRes;
    }
    
    @Deprecated
    public List<String> getBestCustomers(){
        List<String> best = new ArrayList<String>();
        int res = 0;
        for (String cust : getAllCustomers()) {
            if (getReservationsBy(cust).size() == res) {
                best.add(cust);
            }
            if (getReservationsBy(cust).size() > res) {
                res = getReservationsBy(cust).size();
                best.clear();
                best.add(cust);
            }
        }
        return best;
    }

    @Deprecated
    private Set<String> getAllCustomers() {
        List<Reservation> reservations = getAllReservations();
        Set<String> customers = new HashSet<String>();
        for (Reservation reservation : reservations) {
            customers.add(reservation.getCarRenter());
        }
        return customers;
    }
    
    @Deprecated
    private List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<Reservation>();
        for (Car car : cars) {
            reservations.addAll(car.getReservations());
        }
        return reservations;
    }
    
    public void addCarType(CarType carType){
        carTypes.add(carType);
    }
    
    public void addCar(Car car){
        cars.add(car);
    }
}