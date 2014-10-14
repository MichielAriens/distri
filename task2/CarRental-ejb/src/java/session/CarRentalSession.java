package session;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {
    
    private Set<Quote> quotes = new HashSet<Quote>();
    private String clientName = "";

    @Override
    public Set<String> getAllRentalCompanies() {
        System.out.println("passed:  get all ...");
        return new HashSet<String>(RentalStore.getRentals().keySet());
        
    }

    @Override
    public Quote createQuote(ReservationConstraints cons, String rentalCompany) throws ReservationException{
        System.out.println("!!!PASSED!!!");
        CarRentalCompany crc = RentalStore.getRentals().get(rentalCompany);
        Quote quote = crc.createQuote(cons, clientName);
        quotes.add(quote);
        return quote;
    }

    @Override
    public Set<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public void confirmQuotes() throws ReservationException {
       Set<Reservation> confirmedReservations = new HashSet<Reservation>();
        try{
            for(Quote quote: quotes){
                CarRentalCompany crc = RentalStore.getRentals().get(quote.getRentalCompany());
                confirmedReservations.add(crc.confirmQuote(quote));
            }
        }
        catch(ReservationException e){
            for(Reservation reservation: confirmedReservations){
                CarRentalCompany crc = RentalStore.getRentals().get(reservation.getRentalCompany());
                crc.cancelReservation(reservation);
            }
                throw new ReservationException("Confirm quotes failed for: " + clientName);
        }finally{
            quotes = new HashSet<Quote>();
        }
    }

    @Override
    public void setClientName(String name) {
        this.clientName = name;
    }

    @Override
    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> retval = new HashSet<CarType>();
        for(CarRentalCompany crc : RentalStore.getRentals().values()){
            retval.addAll(crc.getAvailableCarTypes(start, end));
        }
        return retval;
        //CarRentalCompany crc = RentalStore.getRentals().get(company);
        //return crc.getAvailableCarTypes(start, end);
    }
}
