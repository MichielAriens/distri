package client;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import rental.ReservationConstraints;
import rental.ReservationException;
import session.CarRentalSessionRemote;

public class Main{
    
    @EJB
    static CarRentalSessionRemote session;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("found rental companies: "+session.getAllRentalCompanies());
            
            
            session.setClientName("Michiel");
            ReservationConstraints cons = new ReservationConstraints(new Date(2014, 10, 1), new Date(2014, 10, 10), "PREMIUM");
            session.createQuote(cons, "hertz");
            System.out.println(session.getCurrentQuotes());
            session.confirmQuotes();
            
            
        } catch (ReservationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
