/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author arun
 */
public class Query {

	public static String place;
	public static double longitude,latitude,timediff;
	public static String timezone = new String();
	public Query(String place) 
	{
		this.place=place;
	}
	/* May need to update the 
	 * following method 
	 * may be to _List_
	 */
	public static String getFeatureId()
	{

		String featureId=new String();
		//double longitude=-1,latitude=-1;
		Connection c = null;
		Statement stmt = null;
		System.out.println(place);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:./test.db");
			c.setAutoCommit(true);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM atlas_alias where name=\""+place+"\"");
			//ResultSet rs = stmt.executeQuery("SELECT * FROM atlas_alias where name=\""+"Bhadravati"+"\"");
			while ( rs.next() ) {
				featureId = rs.getString("country_code");
				latitude = rs.getInt("latitude");
				longitude = rs.getInt("longitude");
			}
			/*featureId = rs.getString("country_code");
			  latitude = rs.getInt("latitude");
			  longitude = rs.getInt("longitude");*/
			rs.close();
			stmt.close();
			c.close();
		} 
		catch(Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		return featureId;

	}
	public String getPlace()
	{
		return place;
	}
	public double getLatitude()
	{
		return	latitude ;
	}
	public double getLongitude()
	{
		return longitude ;
	}
	public double getTimeZone()
	{
		return timediff;
	}

	public static void main(String args[]) {
		Connection c=null;
		Statement stmt=null;
		String fid = new String("");

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:~/test.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * from timezones where country_code=\""+fid+"\"");
			while ( rs.next() ) {
				timediff = rs.getInt("offset");
			}
			rs.close();
			stmt.close();
			c.close();
		} 
		catch(Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
}
