/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michiel
 */
public class CompanyLoader {
    
    private String name;
    private String path;
    
    public CompanyLoader(String name, String path){
        this.name = name;
        this.path = path;
    }

    public CarRentalCompany loadRental() {
        try {
            List<Car> cars = loadData(path);
            return new CarRentalCompany(name, cars);
        } catch (NumberFormatException ex) {
            Logger.getLogger(CompanyLoader.class.getName()).log(Level.SEVERE, "bad file", ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(CompanyLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static List<Car> loadData(String datafile)
            throws NumberFormatException, IOException {

        List<Car> cars = new LinkedList<Car>();
       
        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(CompanyLoader.class.getClassLoader().getResourceAsStream(datafile)));
        //while next line exists
        while (in.ready()) {
            //read line
            String line = in.readLine();
            //if comment: skip
            if (line.startsWith("#")) {
                continue;
            }
            //tokenize on ,
            StringTokenizer csvReader = new StringTokenizer(line, ",");
            //create new car type from first 5 fields
            CarType type = new CarType(csvReader.nextToken(),
                    Integer.parseInt(csvReader.nextToken()),
                    Float.parseFloat(csvReader.nextToken()),
                    Double.parseDouble(csvReader.nextToken()),
                    Boolean.parseBoolean(csvReader.nextToken()));
            //create N new cars with given type, where N is the 5th field
            for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                cars.add(new Car(type));
            }
        }

        return cars;
    }
    
}
