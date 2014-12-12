package ds.gae;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.QueueFactory;

import ds.gae.entities.QuoteBatch;

public class Worker extends HttpServlet {
	private static final long serialVersionUID = -7058685883212377590L;
	
	/**
	 * given a batch id, worker will pull the relevant batch from the store. If already processed nothing will happen.
	 * confirmQuotes will be tried. The success of the confirmation is stored in metadata.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		System.out.print("Preparing to process batch: ");
		
		long key = Long.parseLong(req.getParameter("objectKey"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			QuoteBatch quotes = em.find(QuoteBatch.class, key);
			if (quotes == null){
				System.out.println("Batch not found!");
				return;
			}
			try{
				System.out.println("" + quotes.getId());
				if(quotes.isProcessed()){
					System.out.println("Batch already processed, terminating...");
					return;
				}
				quotes.markProcessed();
				CarRentalModel.get().confirmQuotes(quotes.getAllQuotes());
				System.out.println("SUCCESS");
				quotes.markSuccess(true);
			}catch(ReservationException e){
				System.out.println("Reservation exception!");
				quotes.markSuccess(false);
			}
		}finally {
			System.out.println("Done...");
			em.close();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		doPost(req, resp);
		
	}
}
