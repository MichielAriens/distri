package ds.gae;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ds.gae.entities.QuoteBatch;

public class Worker extends HttpServlet {
	private static final long serialVersionUID = -7058685883212377590L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		System.out.println("Processing batch");
		
		long key = Long.parseLong(req.getParameter("objectKey"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			QuoteBatch quotes = em.find(QuoteBatch.class, key);
			try{
				quotes.markProcessed();
				CarRentalModel.get().confirmQuotes(quotes.getAllQuotes());
				quotes.markSuccess(true);
			}catch(ReservationException e){
				quotes.markSuccess(false);
			}
			em.flush();
			
			
		}finally {
			em.close();
		}
	}
}
