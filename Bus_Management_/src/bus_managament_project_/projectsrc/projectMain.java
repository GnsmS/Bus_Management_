package projectsrc;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class projectMain {
	public static void main(String[] args) throws FileNotFoundException {
		Scanner input = new Scanner(System.in);
		boolean exit=false;
		while(exit!=true) {
			System.out.println("Select Option Number.\n" + "1. Find shortest paths between two stops.\n"
					+ "2. Find a bus stop.\n" + "3. Get all trips between a given time.\n"+"4. Quit");
			try {
				int option = input.nextInt();
				if (option == 1) {
					System.out.println("Enter Bus Stop Numbers(2):\n");
					if (!input.hasNextInt()) {
						System.out.println("Invalid Values, Please Try Again.\n");
					}
					int stop1 = input.nextInt();
					int stop2 = input.nextInt();
					PathCalculater sp = new PathCalculater("projectsrc/transfers.txt", "projectsrc/stop_times.txt", "projectsrc/stops.txt");
					try {
						System.out.println(sp.getPathSequence(stop1, stop2));		
					}
					catch(Exception ArrayIndexOutOfBoundsException) {
						System.out.println("Invalid Values, Please Try Again.\n");
					}
				} 
				else if (option == 2) {
					System.out.println("ENTER BUS TOP NAME (IN CAPS):\n");
					String tmp=input.nextLine();
					assert tmp=="2";
					String stopName = input.nextLine();
					Scanner scanner = new Scanner(new File("projectsrc/stops.txt")).useDelimiter(",");
					ArrayList<String> stops = new ArrayList<String>();
					ArrayList<String> stopNames = new ArrayList<String>();
					String newString="";
					StopSearching a = new StopSearching();
					int key = 9;
					while(scanner.hasNext())
					{
						String temp = scanner.next();
						stops.add(temp);
					}
					for(int i=11; i<stops.size(); i+=9)
					{
						stopNames.add(stops.get(i));
					}
					for(int i=0; i<stopNames.size(); i++)
					{
						if(stopNames.get(i).charAt(2) == ' ')
						{
							newString = stopNames.get(i).substring(3) + " " + stopNames.get(i).substring(0, 2);
							stopNames.set(i, newString);
						}
						a.insert(stopNames.get(i), key);
						key += 9;
					}
					a.match(stopName,stops);
				}
				else if (option == 3) {

					System.out.println("Enter arrival time in the format (HH:MM:SS):\n");
					String arrivalTime = input.next();	
					if(!Character.isDigit(arrivalTime.charAt(0))||!Character.isDigit(arrivalTime.charAt(1))||arrivalTime.charAt(2)!=':'||
							!Character.isDigit(arrivalTime.charAt(3))||!Character.isDigit(arrivalTime.charAt(4))||arrivalTime.charAt(5)!=':'||
							!Character.isDigit(arrivalTime.charAt(6))||!Character.isDigit(arrivalTime.charAt(7))) {
						System.out.println("Invalid Values Entered, Please enter the arrival time in the correct format.\n");
					}
					else {
						String outputString="";
						ArrayList<String[]> returnList= FindRide.searchRide(arrivalTime);
							System.out.println("Trip ID\t\tArrival time\tDeparture time\tStop ID\tStop Sequence\tDropoff type\tShape Distance Travelled");
							for(int i=0;i<returnList.size();i++) {
								for(int j=0;j<8;j++) {
									if(j==5) {
										outputString+="\t";
									}
									else if(j==6) {
										outputString+=returnList.get(i)[j]+",\t\t";
									}
									else {
										outputString+=returnList.get(i)[j]+",\t";
									}
								}
								if(i==returnList.size()-1) {
									outputString=outputString.substring(0, outputString.length()-2)+"\n";
								}
								else outputString+="\n";
							}
							System.out.println(outputString);
					}
				}
				else if(option == 4) {
					System.out.println("Goodbye!");
					exit=true;
				}
				else System.out.println("Invalid Input Entered, Please select a valid option\n");
			}
			catch(Exception InputMismatchException) {
				System.out.println("Error invalid input,please try again\n");
				input.nextLine();
			}
		}
		input.close();
	}
	
}