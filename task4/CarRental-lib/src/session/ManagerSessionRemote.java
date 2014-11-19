package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public int getNumberOfReservationsBy(String renter);
    
    public void addCarRentalCompany(String name, String configFile);

    public CarType getMostPopularCarTypeIn(String carRentalCompanyName);

    public Set<String> getBestClients();

    public void addCarType(String company, String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed);

    public void addCar(String company, String carType);
}