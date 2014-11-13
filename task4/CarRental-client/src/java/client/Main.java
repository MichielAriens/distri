package client;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import rental.CarType;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;
import java.util.logging.Level;
import java.util.logging.Logger;
import rental.ReservationException;

public class Main extends AbstractScriptedTripTest<CarRentalSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) throws Exception{
        super(scriptFile);
        ManagerSessionRemote ses = getNewManagerSession("manager", "none");
        ses.addCarRentalCompany("Hertz", "hertz.csv");
        ses.addCarRentalCompany("Dockx", "dockx.csv");
    }

    public static void main(String[] args) throws Exception {
        //TODO: use updated manager interface to load cars into companies
        new Main("trips").run2();
    }
    
    private void run2(){
        final ReservationConstraints cons = new ReservationConstraints(new Date(2014,10,30), new Date(2014,10,31), "Special");
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    CarRentalSessionRemote ses = getNewReservationSession("Bob");   
                    ses.createQuote("Hertz", cons);
                    ses.confirmQuotes();
                }catch(ReservationException e){
                    System.err.println("Reservation exception as expected.");
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }  
        });
        
        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    CarRentalSessionRemote ses = getNewReservationSession("Jeff");
                    ses.createQuote("Hertz", cons);
                    ses.confirmQuotes();
                }catch(ReservationException e){
                    System.err.println("Reservation exception as expected.");
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }  
        });
        
        t1.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        t2.start();
    }
    
    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        CarRentalSessionRemote out = (CarRentalSessionRemote) new InitialContext().lookup(CarRentalSessionRemote.class.getName());
        out.setRenterName(name);
        return out;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        ManagerSessionRemote out = (ManagerSessionRemote) new InitialContext().lookup(ManagerSessionRemote.class.getName());
        return out;
    }
    
    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        System.out.println("Available car types between "+start+" and "+end+":");
        for(CarType ct : session.getAvailableCarTypes(start, end))
            System.out.println("\t"+ct.toString());
        System.out.println();
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String carRentalName) throws Exception {
        session.createQuote(carRentalName, new ReservationConstraints(start, end, carType));
    }

    @Override
    protected void confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        session.confirmQuotes();
    }
    
    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String renterName) throws Exception {
        return ms.getNumberOfReservationsBy(renterName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String name, String carType) throws Exception {
        return ms.getNumberOfReservations(name, carType);
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName) throws Exception {    
        return ms.getMostPopularCarTypeIn(carRentalCompanyName);
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        return session.getCheapestCarType(start, end);
    }
}