/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.Reservation;

/**
 *
 * @author Julie
 */
@Remote
public interface ManagerSessionRemote {
    Set<Reservation> getReservationsFor(String client);
}

    
