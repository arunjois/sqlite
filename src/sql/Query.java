/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;
import org.sqlite.*;
import java.sql.*;
/**
 *
 * @author arun
 */
public class Query {

	public static String place;
	public static int longitude,latitude,timediff;
	public static String timezone = new String();
	public Query(String place) 
	{
		this.place=place;
	}
	/* May need to update the 
	* following method 
	* may be to _List_
	*/
	public int getLatitude()
	{
		return	latitude ;
	}
	public int getLongitude()
	{
		return longitude ;
	}
	public int getTimeZone()
	{
		return timediff;
	}
	public static void main(String args[])
	{
		
		Connection c = null;
		Statement stmt = null;
		System.out.println(place);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:~/test.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM geonames WHERE name=\""+place+"\";" );
			while ( rs.next() ) {
				longitude = rs.getInt("longitude");
				latitude = rs.getInt("latitude");
				timezone = rs.getString("timezone");
			}
			rs = stmt.executeQuery("SELECT * FROM timezones WHERE=\""+timezone+"\";");
			while(rs.next()){
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
		System.out.println("Operation done successfully");
	}
}
