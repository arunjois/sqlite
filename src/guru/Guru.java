/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guru;
import sql.Query;
import swisseph.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
 *
 * @author arun
 *
 * 
 */
public class Guru {

	/**
	 * @param args the command line arguments
	 */
	private static final int SID_METHOD = SweConst.SE_SIDM_LAHIRI;
	static String toHMS(double d) {
		d += 0.5/3600.;	// round to one second
		int h = (int) d;
		d = (d - h) * 60;
		int min = (int) d;
		int sec = (int)((d - min) * 60);
		return String.format("%2d:%02d:%02d", h, min, sec);
	}

	static String toDMS(double d) {
		d += 0.5/3600./10000.;	// round to 1/1000 of a second
		int deg = (int) d;
		d = (d - deg) * 60;
		int min = (int) d;
		d = (d - min) * 60;
		double sec = d;
		return String.format("%3d°%02d'%07.4f\"", deg, min, sec);
	}
	public static void main(String[] args) {
		int day=0,month=0,year=0;
		int gender=-1;
		double time=0,timezone=0,log=0,lat=0;
		String Place = new String();
		System.out.println(args[0]);
		try {
			int count=0;
			File file = new File(args[0]);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				count++;
				stringBuffer.append(line);
				stringBuffer.append("\n");
				switch(count)
				{
					case 1: month = Integer.parseInt(line);
						break;
					case 2: day = Integer.parseInt(line);
						break;
					case 3: year = Integer.parseInt(line);
						break;
					case 4: time = Double.parseDouble(line);
						break;
					case 6: log = Double.parseDouble(line);
						break;
					case 7: lat = Double.parseDouble(line);
						break;
					case 9: timezone = Double.parseDouble(line);
						break;
					case 13:Place = line;
						System.out.println(Place);
						break;
					case 18:gender = Integer.parseInt(line);
						break;
				}			
			}
			fileReader.close();

			//System.out.println(stringBuffer.toString());
		} catch (IOException e) {
			System.out.print(e);
		}
		

		System.out.println("Contents of file:");
		System.out.println(month);
		System.out.println(day);
		System.out.println(year);
		System.out.println(time);
		System.out.println(timezone);
		System.out.println(log);
		System.out.println(lat);
		System.out.println(gender);
		System.out.println(Place);
		
		
		Query q = new Query(Place);
		
	       
	
		
		timezone=q.getTimeZone();
		time = time + timezone;
		
		
		System.out.println("Contents of file:");
		System.out.println(month);
		System.out.println(day);
		System.out.println(year);
		System.out.println(time);
		System.out.println(timezone);
		System.out.println(log);
		System.out.println(lat);
		System.out.println(gender);
		
		/*
		* Function to check the equivalent 
		* GMT time, date for the given date
		*/
		if(time < 0 || time>=24)
		{
			if (time < 0)
			{
				;
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		SwissEph sw = new SwissEph();
		SweDate sd = new SweDate(year,month,day,time);
		sw.swe_set_sid_mode(SID_METHOD, 0, 0);
		System.out.println(sd.getHour()); 
		// Some required variables:
		double[] cusps = new double[13];
		double[] acsc = new double[10];
		double[] xp= new double[6];
		StringBuffer serr = new StringBuffer();

		double latitude = lat , longitude = log; 
		
		// Print input details:
		System.out.println("Date (YYYY/MM/DD): " + sd.getYear() + "/" + 
				sd.getMonth() + "/" + sd.getDay() + ", " + 
				toHMS(sd.getHour()));
		System.out.println("Jul. day:  " + sd.getJulDay());
		System.out.println("DeltaT:    " + sd.getDeltaT()*24*3600 + " sec.");
		System.out.println("Location:  " +
				toDMS(Math.abs(longitude)) + (longitude > 0 ? "E" : "W") +
				" / " +
				toDMS(Math.abs(latitude)) + (latitude > 0 ? "N" : "S"));

		// Get and print ayanamsa value for info:
		double ayanamsa = sw.swe_get_ayanamsa_ut(sd.getJulDay());
		//System.out.println(ayanamsa);
		System.out.println("Ayanamsa:  " + toDMS(ayanamsa) + " (" + sw.swe_get_ayanamsa_name(SID_METHOD) + ")");

		// Get and print lagna:
		int flags = SweConst.SEFLG_SIDEREAL;
		int result = sw.swe_houses(sd.getJulDay(),
				flags,
				latitude,
				longitude,
				'P',
				cusps,
				acsc);
		System.out.println("Ascendant: " + toDMS(acsc[0]) + "\n");
		int ascSign = (int)(acsc[0] / 30) + 1;
		System.out.println("Ascendant: " +acsc[0] + "\n" + ascSign);
		

		// Calculate all planets:
		int[] planets = { SweConst.SE_SUN,
			SweConst.SE_MOON,
			SweConst.SE_MARS,
			SweConst.SE_MERCURY,
			SweConst.SE_JUPITER,
			SweConst.SE_VENUS,
			SweConst.SE_SATURN,
			SweConst.SE_TRUE_NODE };	// Some systems prefer SE_MEAN_NODE

		flags = //SweConst.SEFLG_SWIEPH |		// fastest method, requires data files
			SweConst.SEFLG_SIDEREAL |	// sidereal zodiac
			SweConst.SEFLG_NONUT |		// will be set automatically for sidereal calculations, if not set here
			SweConst.SEFLG_SPEED;		// to determine retrograde vs. direct motion
		int sign;
		int house;

		boolean retrograde = false;

		for(int p = 0; p < planets.length; p++) {
			int planet = planets[p];
			String planetName = sw.swe_get_planet_name(planet);
			int ret = sw.swe_calc_ut(sd.getJulDay(),
					planet,
					flags,
					xp,
					serr);
			//System.out.println(flags);
			if (ret != flags) {
				if (serr.length() > 0) {
					System.err.println("Warning: " + serr);
				} else {
					System.err.println(
							String.format("Warning, different flags used (0x%x)", ret));
				}
			}

			sign = (int)(xp[0] / 30) + 1;
			house = (sign + 12 - ascSign) % 12 +1;
			retrograde = (xp[3] < 0);

			System.out.printf("%-12s: %s %c; sign: %2d; %s in house %2d\n",
					planetName, toDMS(xp[0]), (retrograde ? 'R' : 'D'), sign, toDMS(xp[0] % 30), house);
		}
		// KETU
		xp[0] = (xp[0] + 180.0) % 360;
		String planetName = "Ketu (true)";

		sign = (int)(xp[0] / 30) + 1;
		house = (sign + 12 - ascSign) % 12 +1;

		System.out.printf("%-12s: %s %c; sign: %2d; %s in house %2d\n",
				planetName, toDMS(xp[0]), (retrograde ? 'R' : 'D'), sign, toDMS(xp[0] % 30), house);
	}

}


