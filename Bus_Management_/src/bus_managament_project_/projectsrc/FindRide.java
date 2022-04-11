package projectsrc;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;


//Takes search term in string HH:MM:SS format
//Returns arrayList full of arrays
// So arrayList.get(0) would contain an array with first line data
// So arrayList.get(0)[0] would contain first field in first line#
// Also checks lines in file for errors
public class FindRide {
	public static ArrayList<String[]> searchRide(String inputVals) {
		ArrayList<String[]> resultss = new ArrayList<>(0);
		if(FindRide.verifyTime(inputVals)){
			return resultss;
		}
		try {
        File map = new File("projectsrc/stop_times.txt");
	    Scanner myReader = new Scanner(map);
    	myReader.nextLine();
        while (myReader.hasNextLine()) {
          String[] currentLine = myReader.nextLine().split(",");
          if(currentLine.length>2){
          if(currentLine[1].replace(" ","0").equals(inputVals)){
        	  String[] tempArray = {currentLine[0],currentLine[1].replace(" ","0"),currentLine[2].replace(" ","0"),currentLine[3]
                      ,currentLine[4],currentLine[5],currentLine[6],currentLine[7]};
              resultss.add(tempArray);
          }}
        }
        myReader.close();  } catch (FileNotFoundException ex){
            return resultss;
        }
        return resultss;
}
    private static Boolean verifyTime(String time){
        String[] timeSplit = time.split(":");
        if(timeSplit.length != 3){
            return false;
        }
        int hour = Integer.parseInt(timeSplit[0]);
        if(hour>24 || hour<0){
            return false;
        }
        int minute = Integer.parseInt(timeSplit[1]);
        if(minute < 0 || minute > 60){
            return false;
        }
        int second = Integer.parseInt(timeSplit[2]);
        if(second < 0 || second >60){
            return false;
        }
        return true;
        

    }

}
