package session;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession extends Session implements CarRentalSessionRemote {
    
    @Resource
    private EJBContext context;

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        Query query = em.createNamedQuery("CarRentalCompany.findAllNames");
        List<String> results = query.getResultList();
        return new HashSet<String>(results);
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        return em.createNamedQuery("CarType.getAvailable", CarType.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        try {
            Quote out = getCompany(company).createQuote(constraints, renter);
            quotes.add(out);
            return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                Reservation res = getCompany(quote.getRentalCompany()).confirmQuote(quote);
                if(em.createNamedQuery("Car.getReservationsAt")
                        .setParameter("carId", res.getCarId())
                        .setParameter("start", res.getStartDate())
                        .setParameter("end", res.getEndDate())
                        .getResultList()
                        .size() == 1){
                    done.add(res);
                }else{
                    throw new ReservationException("Concurrent confirm attempted. Aborting one.");
                }
            }
        } catch (ReservationException e) {
            context.setRollbackOnly();
            throw e;
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getCheapestCarType(Date start, Date end) {
        List<CarType> availableTypes = em.createNamedQuery("CarType.getAvailable",CarType.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
        availableTypes.sort(new Comparator<CarType>(){
            @Override
            public int compare(CarType o1, CarType o2) {
                return Double.compare(o1.getRentalPricePerDay(), o2.getRentalPricePerDay());
            }  
        });
        System.err.println("findme");
        for(CarType t : availableTypes){
            System.err.println(t.toString());
        }
        if(availableTypes.isEmpty())
            return null;
        else{
            return availableTypes.get(0).getName();
        }
        
    }
}