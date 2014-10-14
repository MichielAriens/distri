package client;

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import rental.Reservation;
import javax.naming.InitialContext;
import rental.ReservationConstraints;
import rental.ReservationException;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractScriptedSimpleTripTest<CarRentalSessionRemote, ManagerSessionRemote>{
    
    //@EJB
    //static CarRentalSessionRemote session;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //System.out.println("found rental companies: "+session.getAllRentalCompanies());
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext context = new InitialContext();
        CarRentalSessionRemote session = (CarRentalSessionRemote) context.lookup(CarRentalSessionRemote.class.getName());
        session.setClientName(name);
        return session;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        InitialContext context = new InitialContext();
        ManagerSessionRemote session = (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
        session.setClientName(name);
        return session;
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String carRentalName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        Set<Reservation> reservations = ms.getReservationsFor(clientName);
        int numberOfRes = 0;
        for(Reservation reservation: reservations){
            numberOfRes++;
        }
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCarType(carRentalName, carType);
    }
    
    /**
     * try {
            System.out.println("found rental companies: "+session.getAllRentalCompanies());
            
            
            session.setClientName("Michiel");
            ReservationConstraints cons = new ReservationConstraints(new Date(2014, 10, 1), new Date(2014, 10, 10), "Premium");
            session.createQuote(cons, "Hertz");
            System.out.println(session.getCurrentQuotes());
            session.confirmQuotes();
            
            
        } catch (ReservationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        **/
    
}
