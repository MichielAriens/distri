package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.Quote;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface CarRentalSessionRemote {
    
    void setClientName(String name);

    Set<String> getAllRentalCompanies();
    
    Quote createQuote(ReservationConstraints cons, String rentalCompany) throws ReservationException;
    
    Set<Quote> getCurrentQuotes();
    
    void confirmQuotes() throws ReservationException;
    
    
    
}
