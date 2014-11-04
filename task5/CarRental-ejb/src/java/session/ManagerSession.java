/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;


import java.util.HashSet;
import rental.Reservation;

import java.util.Set;
import javax.ejb.Stateless;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;


/**
 *
 * @author Julie & Michiel
 */
@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @Override
    public Set<Reservation> getReservationsFor(String client) {
        Set<Reservation> retval = new HashSet<Reservation>();
        for(CarRentalCompany crc : RentalStore.getRentals().values()){
            retval.addAll(crc.getReservationsFor(client));
        }
        return retval;
    }
    
    public Set<CarType> getCarTypesForCompany(String rentalCompany) {
        CarRentalCompany crc = RentalStore.getRentals().get(rentalCompany);
        return new HashSet<CarType>(crc.getAllTypes());
    }

    @Override
    public int getNumberOfReservationsForCarType(String rentalCompany, String carType) {
        CarRentalCompany crc = RentalStore.getRentals().get(rentalCompany);
        Set<Reservation> reservations = crc.getReservationsForCarType(carType);
        int numberOfRes = 0;
        for(Reservation reservation: reservations){
            if(reservation.getCarType().equals(carType)){
                numberOfRes ++;
            }
        }
        return numberOfRes;
    }

}
