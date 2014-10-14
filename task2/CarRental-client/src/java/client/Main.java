package client;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractScriptedSimpleTripTest<CarRentalSessionRemote, ManagerSessionRemote>{
    
    //@EJB
    //static CarRentalSessionRemote session;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //System.out.println("found rental companies: "+session.getAllRentalCompanies());
            Main main = new Main("simpleTrips");
            main.run();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Exception caught at top level.");
            ex.printStackTrace();
        }
    }

    public Main(String scriptFile) {
        super(scriptFile);
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
        return session;
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        //For all systems. TODO: Check this
        System.out.println(session.getAvailableCarTypes(start, end));
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String carRentalName) throws Exception {
        ReservationConstraints cons = new ReservationConstraints(start, end, carType);
        session.createQuote(cons, carRentalName);
    }

    @Override
    protected void confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getReservationsFor(clientName).size();
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCarType(carRentalName, carType);
    }
    
    
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
    