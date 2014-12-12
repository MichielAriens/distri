package ds.gae.entities;


import java.util.List;




import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Extends Stored object with metadata.  
 * @author Michiel
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class QuoteBatch extends StoredObject {
	
	private boolean processed = false;
	private boolean successful = false;
	
	public QuoteBatch(){
		super();
	}
	
	public QuoteBatch(List<Quote> allQuotes){
		super(allQuotes);
	}
	
	
	
	public List<Quote> getAllQuotes(){
		return (List<Quote>) this.getStoredObject();
	}

	public void markProcessed(){
		this.processed = true;
	}
	
	public void markSuccess(boolean val){
		if(isProcessed())
			this.successful = val;
	}
	
	public boolean isProcessed(){
		return processed;
	}
	
	public boolean wasSuccessful(){
		return successful;
	}
}
