/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.CarRentalCompany;

/**
 *
 * @author michiel
 */
public class Session {
    
    @PersistenceContext
    EntityManager em;
    
    protected CarRentalCompany getCompany(String name){
        Query query = em.createQuery("SELECT e FROM CarRentalCompany e WHERE e.name LIKE :compName").setParameter("compName", name).setMaxResults(1);
        List<Object> results = query.getResultList();
        if(results.isEmpty()){
            return null;
        }else if(results.size() == 1){
            return (CarRentalCompany) results.get(0);
        }else{
            //TODO define CarRentalCompany.name as unique.
            throw new RuntimeException("More than one result found for a unique field");
        }
    }
    
    protected Collection<CarRentalCompany> getAllCompanies(){
        Query query = em.createQuery("SELECT e FROM CarRentalCompany e");
        List<Object> results = query.getResultList();
        return (List<CarRentalCompany>)(List<?>) results;//Hacky solution ok, we know all objects will be CarRentalCompanies
    }
    
}
