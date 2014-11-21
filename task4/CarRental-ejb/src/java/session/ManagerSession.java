package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.print.attribute.HashAttributeSet;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.CompanyLoader;
import rental.Reservation;

@Stateless
public class ManagerSession extends Session implements ManagerSessionRemote {
    
    
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        List<CarType> results = em.createNamedQuery("CarRentalCompany.findAllCarTypes").getResultList();
        return new HashSet<CarType>(results);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        return new HashSet<Integer>(em.createNamedQuery("Car.getAllIds",Integer.class).getResultList());
    }

    @Override
    public int getNumberOfReservations(String company, int id) {
        try {
            CarRentalCompany crc = getCompany(company);
            return crc.getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        long retval = em.createNamedQuery("Reservation.countPerTypeAndCompany", Long.class).
                setParameter("company", company).
                setParameter("type", type).
                getSingleResult();
        return (int) retval;
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        long retval = em.createNamedQuery("Reservation.countPerCustomer", Long.class).
                setParameter("customer", renter).
                getSingleResult();
        return (int) retval;
    }

    @Override
    public void addCarRentalCompany(String name, String configFile) {
        CompanyLoader loader = new CompanyLoader(name, configFile);
        CarRentalCompany crc = loader.loadRental();
        em.persist(crc);
    }
    
    @Override
    public void addCarType(String company, String name,int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed){
        CarRentalCompany crc = em.createNamedQuery("CarRentalCompany.getByName",CarRentalCompany.class)
                .setParameter("compName", company).getSingleResult();
        CarType carType = new CarType(name, nbOfSeats, trunkSpace, rentalPricePerDay, smokingAllowed);
        crc.addCarType(carType);
        //em.persist(crc);
    }
    
    @Override
    public void addCar(String company, String carType){
        CarRentalCompany crc = em.createNamedQuery("CarRentalCompany.getByName",CarRentalCompany.class)
                .setParameter("compName", company).getSingleResult();
        CarType type = em.createNamedQuery("CarType.getByName",CarType.class)
                .setParameter("name", carType).getSingleResult();
        Car car = new Car(type);
        crc.addCar(car);
        //em.persist(crc);
    }


    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName) {
        String carTypeName = em.createNamedQuery("Reservation.getBestType", String.class)
                .setParameter("company", carRentalCompanyName)
                .setMaxResults(1)
                .getResultList()
                .get(0);
        return em.createNamedQuery("CarType.getByName", CarType.class).setParameter("name", carTypeName).getSingleResult();
    }
    
    /**
     * results = list<X> with for every element X: X[0]: String and X[1]: int ordered so that results.get(O)[1] is the highest int in the list
     */
    private Set<String> getBest(List<Object[]> results){
        Set<String> retval = new HashSet<String>();
        if(results.isEmpty())
            return retval;
        long max = (Long) results.get(0)[1];
        for(Object[] o : results){
            if (((Long) o[1]).equals(max)){
                retval.add((String) o[0]);
            }else{
                break;
            }
        }
        return retval;
    }

    @Override
    public Set<String> getBestClients() {
        List<Object[]> results = em.createNamedQuery("Reservation.getBestCustomer").getResultList();
        return getBest(results);
    }
    
    
    
    
}