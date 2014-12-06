package ds.gae.entities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Serialized;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Blob;

@Entity
public class QuoteBatch {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long objectId;
	
	private boolean processed = false;
	private boolean successful = false;
	
	private List<Quote> allQuotes;
	
	public QuoteBatch(){
	}
	
	public QuoteBatch(List<Quote> allQuotes){
		this.allQuotes = allQuotes;
	}
	
	public long getId(){
		return objectId;
	}
	
	public List<Quote> getAllQuotes(){
		return this.allQuotes;
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
		if(!this.processed)
			throw new RuntimeException("Call to wasSuccessful() in QuoteBatch while not yet processed!");
		return successful;
	}
}
