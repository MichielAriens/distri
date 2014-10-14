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
 * @author Julie
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
        return (Set)crc.getAllTypes();
    }

    @Override
    public int getNumberOfReservationsForCarType(String rentalCompany, String carType) {
        Set<CarType> carTypes = this.getCarTypesForCompany(rentalCompany);
        int numberOfRes = 0;
        for(CarType type: carTypes){
            if(type.getName().equals(carType)){
                numberOfRes ++;
            }
        }
        return numberOfRes;
    }

}
