package ds.gae.servlets;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import ds.gae.EMF;
import ds.gae.entities.Quote;
import ds.gae.entities.QuoteBatch;
import ds.gae.view.JSPSite;

@SuppressWarnings("serial")
public class ConfirmQuotesServlet extends HttpServlet {
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		Map<String, List<Quote>> allQuotes = (Map<String, List<Quote>>) session.getAttribute("quotes");
		
		List<Quote> quotesList = new ArrayList<Quote>();
		for(List<Quote> qs : allQuotes.values()){
			quotesList.addAll(qs);
		}
		
		EntityManager em = EMF.get().createEntityManager();
		em.getTransaction().begin();
		try{
			QuoteBatch obj = new QuoteBatch(quotesList);
			em.persist(obj);
			em.getTransaction().commit();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/worker").param("objectKey", "" + obj.getId()).method(Method.POST).taskName("ConfirmBatch_"+obj.getId()));
			session.setAttribute("batchId", "" + obj.getId());
			resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
		}finally{
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			em.close();
		}
	}
}


/**
try {
TODO: remove
	ArrayList<Quote> qs = new ArrayList<Quote>();
	
	for (String crcName : allQuotes.keySet()) {
		qs.addAll(allQuotes.get(crcName));
	}
	CarRentalModel.get().confirmQuotes(qs);
	
	session.setAttribute("quotes", new HashMap<String, ArrayList<Quote>>());
	resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
	//resp.sendRedirect(JSPSite.CREATE_QUOTES.url());
} catch (ReservationException e) {
	session.setAttribute("errorMsg", ViewTools.encodeHTML(e.getMessage()));
	resp.sendRedirect(JSPSite.RESERVATION_ERROR.url());				
}*/
