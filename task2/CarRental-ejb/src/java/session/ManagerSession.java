/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateless;
import rental.CarRentalCompany;
import rental.RentalStore;
import rental.Reservation;

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
    
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
