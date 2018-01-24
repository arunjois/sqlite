/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guru;
import swisseph.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;




/**
 *
 * @author arun
 */
public class Guru {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int day=0,month=0,year=0;
		int gender=-1;
		double time=0,timezone=0,log=0,lat=0;
		
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
					case 5: timezone = Double.parseDouble(line);
						break;
					case 6: log = Double.parseDouble(line);
						break;
					case 7: lat = Double.parseDouble(line);
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
		SwissEph eph = new SwissEph();
		SweDate sd = new SweDate(year,month,day,time);
		System.out.println(sd.getHour());
	}
	
}
