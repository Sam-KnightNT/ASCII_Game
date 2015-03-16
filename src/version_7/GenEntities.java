package version_7;

import java.io.*;
import java.util.HashMap;

public class GenEntities {

	public static void main(String[] args) throws IOException {
		
		String filename = "C:\\Users\\Sam\\eclipse\\workspace\\ASCII_Game\\entities.txt";
		System.out.println(new File(filename).getAbsolutePath());
		System.out.println(System.getProperty("user.dir"));
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		
		String contents = "";
		String line = reader.readLine();
		while (line != null) {
			contents += line.trim().replaceAll(": ", ":\n")+"\n";
			line = reader.readLine();
		}
		
		reader.close();
		
		System.out.println("Contents: "+contents+"\nEnd");
		
		HashMap<String, Integer> stats = new HashMap<String, Integer>();
		String statString = contents.substring(contents.indexOf("stats:")+7);
		System.out.println("String is "+statString+"\nEnd");
		String[] indivStats = statString.split("\n");
		
		for (String stat:indivStats) {
			if (stat.contains("=")) {
				String[] str = stat.split("=");
				stats.put(str[0].trim(), Integer.parseInt(str[1].trim()));
				System.out.println(str[0]+": "+str[1]);
			} else if (stat.startsWith("no ")) {
				stats.put(stat.substring(3), 0);
				System.out.println(stat.substring(3)+": 0");
			}
		}
		
		
		
		
	}
	
	
}
