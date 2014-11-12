package session;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
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
    
    public CarRentalCompany getCompany(String company){
        Query query = em.createQuery("SELECT e FROM CarRentalCompany e WHERE e.name LIKE :compName").setParameter("compName",company);
        return (CarRentalCompany) query.getResultList().get(0);
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
}