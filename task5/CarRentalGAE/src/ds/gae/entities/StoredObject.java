package ds.gae.entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.appengine.api.datastore.Blob;

@Entity
@MappedSuperclass
public class StoredObject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long objectId;

	private Blob storedObject;

	public StoredObject(){

	}
	
	public long getId(){
		return objectId;
	}

	public StoredObject(Object obj){
		try {
			storedObject = new Blob(serialize(obj));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object getStoredObject(){
		try {
			return deserialize(storedObject.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	private byte[] serialize(Object obj) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(obj);
			out.flush();
			byte[] yourBytes = bos.toByteArray();
			return yourBytes;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {}
			try {
				bos.close();
			} catch (IOException ex) {}
		}
	}

	private Object deserialize(byte[] bytes) throws IOException{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject(); 
			return o;
		} catch (ClassNotFoundException e) {
			// Can't happen;
			e.printStackTrace(); return null;
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {}
		}	
	}
}
