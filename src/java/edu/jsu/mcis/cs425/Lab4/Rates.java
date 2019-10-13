package edu.jsu.mcis.cs425.Lab4;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Rates {
    
    public static final String RATE_FILENAME = "rates.csv";
    
    public static List<String[]> getRates(String path) {
        
        StringBuilder s = new StringBuilder();
        List<String[]> data = null;
        String line;
        
        try {
            
            /* Open Rates File; Attach BufferedReader */

            BufferedReader reader = new BufferedReader(new FileReader(path));
            
            /* Get File Data */
            
            while((line = reader.readLine()) != null) {
                s.append(line).append('\n');
            }
            
            reader.close();
            
            /* Attach CSVReader; Parse File Data to List */
            
            CSVReader csvreader = new CSVReader(new StringReader(s.toString()));
            data = csvreader.readAll();
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return List */
        
        return data;
        
    }
    
    public static String getRatesAsTable(List<String[]> csv) {
        
        StringBuilder s = new StringBuilder();
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create HTML Table */
            
            s.append("<table>");
            
            while (iterator.hasNext()) {
                
                /* Create Row */
            
                row = iterator.next();
                s.append("<tr>");
                
                for (int i = 0; i < row.length; ++i) {
                    s.append("<td>").append(row[i]).append("</td>");
                }
                
                /* Close Row */
                
                s.append("</tr>");
            
            }
            
            /* Close Table */
            
            s.append("</table>");
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return Table */
        
        return (s.toString());
        
    }
    
    public static String getRatesAsJson(List<String[]> csv) {
        
        String results = "";
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create JSON Containers */
            
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();            
            
            boolean first = true;
       
            while(iterator.hasNext()){
                if(first) {
                    iterator.next();
                    row = iterator.next();
                    double numRate = Double.parseDouble(row[2]);
                    rates.put(row[1], numRate);
                    first = false;
                }
                else{
                    row = iterator.next();
                    double numRate = Double.parseDouble(row[2]);
                    rates.put(row[1], numRate);
                }
            }
            json.put("rates", rates);
            Date date = new Date();
            String presentDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            json.put("date", presentDate);
            json.put("base", "USD");
            
            /* Parse top-level container to a JSON string */
            
            results = JSONValue.toJSONString(json);
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return JSON string */
        /*Snellen added this line */
        System.err.println(results);
        
        return (results.trim());
        
    }
    
    public static String getRatesAsJson(String code){
        String returnVal = null;
        try{
            Context envContext = new InitialContext();
            Context initContext = (Context) envContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) initContext.lookup("jdbc/db_pool");
            Connection conn = ds.getConnection();
            PreparedStatement pstatement;
            String query = "";
            ResultSet results;
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();
            if(code == null){
                query = "SELECT * FROM rates r";
                pstatement = conn.prepareStatement(query);
                results = pstatement.executeQuery();
                System.err.println("code was null");
            }
            else{
                query = "SELECT code, rate, date FROM rates r where code like ?";
                pstatement = conn.prepareStatement(query);
                pstatement.setString(1, code);
                results = pstatement.executeQuery();
                System.err.println("code was not null");
            }
            
            String date = "";
            while(results.next()){
                String nextCode; 
                double rate;
                nextCode  = results.getString("code");
                rate = results.getDouble("rate");
                rates.put(nextCode, rate);
                date = results.getDate("date").toString();
            }
            json.put("rates", rates);
            json.put("date",date);
            json.put("base","USD");
            returnVal = JSONValue.toJSONString(json);
            return returnVal;
        } catch (NamingException ex) {
            Logger.getLogger(Rates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Rates.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnVal;
    }

}