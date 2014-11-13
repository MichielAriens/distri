package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.Query;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.CompanyLoader;
import rental.Reservation;

@Stateless
public class ManagerSession extends Session implements ManagerSessionRemote {
    
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            CarRentalCompany crc = getCompany(company);
            return new HashSet<CarType>(crc.getAllTypes());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            CarRentalCompany crc = getCompany(company);
            for(Car c: crc.getCars(type)){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
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
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            CarRentalCompany crc = getCompany(company);
            for(Car c: crc.getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        Set<Reservation> out = new HashSet<Reservation>();
        Query query = em.createQuery("SELECT e FROM CarRentalCompany e");
        for(Object crc : query.getResultList()) {
            out.addAll(((CarRentalCompany)crc).getReservationsBy(renter));
        }
        return out.size();
    }

    @Override
    public void addCarRentalCompany(String name, String configFile) {
        CompanyLoader loader = new CompanyLoader(name, configFile);
        CarRentalCompany crc = loader.loadRental();
        em.persist(crc);
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName) {
        return getCompany(carRentalCompanyName).getMostPopularCarType();
    }

    @Override
    public Set<String> getBestClients() {
        Set<String> best = new HashSet<String>();
        int res = 0;
        for (CarRentalCompany crc : getAllCompanies()) {
            List<String> bestCustomers = crc.getBestCustomers();
            if (!bestCustomers.isEmpty()) {
                int numb = getNumberOfReservationsBy(bestCustomers.get(0));
                if (numb == res) {
                    best.addAll(bestCustomers);
                }
                if (numb > res) {
                    best.clear();
                    best.addAll(bestCustomers);
                    res = numb;
                }
            }
        }

        return best;
    }
    
    
    
    
}