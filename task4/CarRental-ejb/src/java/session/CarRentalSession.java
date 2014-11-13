package session;

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
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import rental.CarRentalCompany;
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
        Query query = em.createQuery("SELECT e FROM CarRentalCompany e");
        Set<String> retval = new HashSet<String>();
        for(Object o : query.getResultList()){
            retval.add(((CarRentalCompany)o).getName());
        }
        return retval;
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarType> availableCarTypes = new LinkedList<CarType>();
        for(String crc : getAllRentalCompanies()) {
            for(CarType ct : getCompany(crc).getAvailableCarTypes(start, end)) {
                if(!availableCarTypes.contains(ct))
                    availableCarTypes.add(ct);
            }
        }
        return availableCarTypes;
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                done.add(getCompany(quote.getRentalCompany()).confirmQuote(quote));
            }
            checkConstraints();
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
        CarType ct = null;
        for (CarType carType : getAvailableCarTypes(start, end)) {
            if (ct == null) {
                ct = carType;
            }
            if (carType.getRentalPricePerDay() < ct.getRentalPricePerDay()) {
                ct = carType;
            }
        }
        return ct.getName();
    }
}