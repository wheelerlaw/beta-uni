/*
 * File: BetaUniversity.java
 * Date: 11/13/14
 * 
 */

import java.sql.*;
import java.util.Scanner;

import org.postgresql.Driver;

/**
 * The main class of the program.
 * @author Wheeler Law <wpl3499@rit.edu>
 *
 */
public class BetaUniversity {
	
	private static Connection c;
	private static Scanner sc;
	
	public static Connection getConnection(){return c;}

	/**
	 * The main operating method of the program. Connects to a server, gets input, 
	 * and quits. 
	 * @param args
	 */
	public static void main(String[] args){
		String host = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/p32001b";
		String user = "p32001b";
		String password = "never eat sour waffles";
		
		// Only run if running this program in eclipse.
		if(EclipseTools.isDevelopmentEnvironment())
			EclipseTools.fixConsole();
		
		sc = new Scanner(System.in);
		
		connect(host, user, password);
		
		System.out.println("Enter command (enter \"help\" for list of commands):");
		getInput();
		
		sc.close();
		quit();
		
	}
	
	/**
	 * Connects to the Postgres server. 
	 * @param host		The hostname of the server
	 * @param user		The username of the server.
	 * @param password	The password for the username. 
	 */
	private static void connect(String host, String user, String password){
		c = null;
		try{
			Class.forName("org.postgresql.Driver");
			Driver driver = new Driver();
			DriverManager.registerDriver(driver);
			c = DriverManager.getConnection(host, user, password);
			
			System.out.println("Opened connection to database.");
			
			/*
			stmt = c.createStatement();
			String sql = "create table hello("+
						"something int"+
						");";
			
			stmt.executeUpdate(sql);
			stmt.close();*/
			//c.close();
		}catch(Exception e){
			System.err.println("Could not connect.");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Closes any open resources.
	 */
	private static void quit(){
		try {
			c.close();
			System.out.println("Closed connection to database.");
		} catch (SQLException e) {
			System.err.println("Could not close connection.");
			e.printStackTrace();
		}
	}
	
	/**
	 * The main driving method of the program. Gets input from the user and calls
	 * various methods that act as abstractions of the various core components 
	 * of this program. 
	 */
	private static void getInput(){
				
		System.out.print("> ");
		
		String line = sc.nextLine().trim();
		String[] splitCommand = line.split(" ");
		
		if(splitCommand[0].equalsIgnoreCase("new")){
			if(splitCommand[1].equalsIgnoreCase("donation")){
				//TODO create new donation
				/*
				System.out.println("Enter donor information:");
				System.out.print("Name: > ");
				System.out.print("YOG: >");
				System.out.print("");*/
				
				System.out.println("> ");
				
			}else if(splitCommand[1].equalsIgnoreCase("donor")){
				//TODO create new donor
				
			}else if(splitCommand[1].equalsIgnoreCase("address")){
				Address address = null;
				boolean valid = false;
				while(!valid){
					address = promptAndCreateAddress();
					if(address.save() == 0){
						valid = true;
					}
				}
				
				
				System.out.println(address);
			}else if(splitCommand[1].equalsIgnoreCase("company")){
				//TODO create new company
			}
		}
		/*
		System.out.println(Donor.open(Integer.parseInt(splitCommand[0])).name);
		int id = Integer.parseInt(splitCommand[0]);
		Donor donor = Donor.open(id);
		donor.name = "Someone";
		System.out.println(donor.save());
		donor.circle = Circle.open(4);
		donor.save();*/
		
		sc.close();
	}
	
	/**
	 * Prompts the user for information about creating a new address row/object.
	 * This method will continue to prompt the user until valid data is entered,
	 * e.g., valid numeric zipcode, valid state abbreviation. 
	 * 
	 * @return Address	an address object if the data was successfully validated. 
	 */
	private static Address promptAndCreateAddress(){
		boolean valid = false;
		Address address = null;
		
		outer:
		while(!valid){
			System.out.println("Enter the following information:");
			System.out.print("Street: > ");
			String street = sc.nextLine().trim();
			
			System.out.print("City: > ");
			String city = sc.nextLine().trim();
			
			System.out.print("State abbrv.: > ");
			String stateStr = sc.nextLine().trim();
			
			System.out.print("Zipcode: > ");
			String zipcode = sc.nextLine().trim();
			
			System.out.println("You have entered the followig information:");
			System.out.println(street);
			System.out.println(city+", "+stateStr+" "+zipcode);
			System.out.print("Is this correct? [Y/n] > ");
			
			boolean validResponse = false;
			
			while(!validResponse){
				String response = sc.nextLine().trim();
				if(response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n")){
					continue outer;
				}else if(response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")){
					break;
				}else{
					System.out.println("What? > ");
				}
			}
			
			System.out.flush();
			
			boolean error = false;
		
			State state = State.open(stateStr);
			if(state == null){
				System.err.println("Invalid state: "+ stateStr);
				error = true;
			}
			
			try{
				Integer.parseInt(zipcode);
			}catch(NumberFormatException e){
				System.err.println("Invalid zipcode: "+zipcode);
				error = true;
			}
			
			if(!error){
				try{
					address = new Address(street, city, state, Integer.parseInt(zipcode));
					valid = true;
				}catch(Exception e){
					System.err.println("Something else went wrong:");
					System.err.println(e.getMessage());
				}
			}
			
		}
		return address;
	}
	
	
}
