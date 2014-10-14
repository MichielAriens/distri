package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.Quote;
import rental.ReservationConstraints;

@Remote
public interface CarRentalSessionRemote {

    Set<String> getAllRentalCompanies();
    
    Quote createQuote(ReservationConstraints cons);
    
    Set<Quote> getCurrentQuotes();
    
}
