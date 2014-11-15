/*
 * File: BetaUniversity.java
 * Date: 11/13/14
 * 
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException{
		String host = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/p32001b";
		String user = "p32001b";
		String password = "never eat sour waffles";
		
		// Only run if running this program in eclipse.
		if(EclipseTools.isDevelopmentEnvironment()){
			EclipseTools.fixConsole();
			//System.setIn(new FileInputStream("input.1"));
		}
		
		sc = new Scanner(System.in);
		
		connect(host, user, password);
		
		
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
	 * @throws  
	 */
	private static void getInput(){
		
		String options = "1.  New donation";
		
		System.out.println("What would you like to do?");
		System.out.println("Enter \"help\" for a list of commands");
		System.out.println("Enter \"quit\" to exit");
		
		boolean quit = false;
		while(!quit){
			
			System.out.print("> ");
			String command = sc.nextLine().trim();
			
			if(command.equalsIgnoreCase("help")){
				System.out.println("List of commands:");
				System.out.println(options);
			}else if(command.equalsIgnoreCase("quit")){
				System.out.println("Exiting...");
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.exit(1);
				}
				quit = true;
				continue;
			}else if(command.equals("1")){
				Donation donation = promptAndCreateDonation();
				Donor donor = promptAndCreateDonor();
				
				
				donation.save();
				donor.save();
				
				Donated donated = null;
				
				try {
					donated = promptAndCreateDonated(donation.id(), donor.id());
				} catch (SQLNotSavedException e) {
					// Shouldn't occur. 
				}
				
				donated.save();
				
				System.out.println("Donation entered sucessfully!");
				System.out.println("Did the donor's company match the donation?");
				if(promptYesOrNo()){
					Company company = promptAndCreateCompany();
					
					company.save();
					
					MatchingCorp matchingCorp = null;
					
					try {
						matchingCorp = promptAndCreateMatchingCorp(donation.id(), donor.id());
					} catch (SQLNotSavedException e) {
						// Shouldn't occur. 
					}
					
					matchingCorp.save();
					
					System.out.println("Matching information entered sucessfully!");
				}
				
			}else if(command.equals("2")){
				getCategories();
			}
			
		}
		/*
				
		System.out.print("> ");
		
		String line = sc.nextLine().trim();
		String[] splitCommand = line.split(" ");
		
		if(splitCommand[0].equalsIgnoreCase("new")){
			if(splitCommand[1].equalsIgnoreCase("donation")){
				Donation donation = null;
				boolean valid = false;
				while(!valid){
					donation = promptAndCreateDonation();
					if(donation.save() == 0){
						valid = true;
					}
				}
				
			}else if(splitCommand[1].equalsIgnoreCase("donor")){
				Donor donor = null;
				boolean valid = false;
				while(!valid){
					donor = promptAndCreateDonor();
					if(donor.save() == 0){
						valid = true;
					}
				}
				
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
				Company company = null;
				boolean valid = false;
				while(!valid){
					company = promptAndCreateCompany();
					if(company.save() == 0){
						valid = true;
					}
				}
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
			System.out.println("Enter address information:");
			System.out.print("Street: > ");
			String street = sc.nextLine().trim();
			
			System.out.print("City: > ");
			String city = sc.nextLine().trim();
			
			System.out.print("State abbrv.: > ");
			String stateStr = sc.nextLine().trim();
			
			System.out.print("Zipcode: > ");
			String zipcode = sc.nextLine().trim();
			
			System.out.println("You have entered this address:");
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
	
	/**
	 * Prompts the user for information about creating a new address row/object.
	 * This method will continue to prompt the user until valid data is entered,
	 * e.g., valid numeric zipcode, valid state abbreviation. 
	 * 
	 * @return Address	an address object if the data was successfully validated. 
	 */
	private static Donor promptAndCreateDonor(){
		boolean valid = false;
		Donor donor = null;
		
		outer:
		while(!valid){
			System.out.println("Enter the following information:");
			System.out.print("Name: > ");
			String name = sc.nextLine().trim();
			
			System.out.print("Spouse name (opt.): > ");
			String nameOfSpouse = sc.nextLine().trim();
			
			
			boolean validYOG = false;
			int yog = 0;
			while(!validYOG){
				System.out.print("Year of grad.: > ");
				String yogStr = sc.nextLine().trim();
				try{
					yog = Integer.parseInt(yogStr);
				}catch(NumberFormatException e){
					System.err.println("Invalid YOG: " + yogStr);
					continue;
				}
				validYOG = true;
			}
			
			
			String[] categories = getCategories();
			
			System.out.println("Categories of donors:");
			System.out.println("ID | Name");
			for(String categoryStr : categories){
				String[] category = categoryStr.split("~");
				
				System.out.println(category[0] + "   " + category[1]);
			}
			
			boolean validCategory = false;
			Category category = null;
			while(!validCategory){
				System.out.print("Entery category ID: > ");
				String categoryString = sc.nextLine().trim();
				int categoryId;
				try{
					categoryId = Integer.parseInt(categoryString);
				}catch(NumberFormatException e){
					System.err.println("Invalid category ID: "+categoryString);
					continue;
				}
				
				category = Category.open(categoryId);
				if(category == null){
					System.err.println("Invalid category ID: "+categoryString);
					continue;
				}
				validCategory = true;
				
			}
			
			Circle circle = Circle.open(1);
			Address address = promptAndCreateAddress();
			
			donor = new Donor(name, nameOfSpouse, yog, category, address, circle);
			
			System.out.println("You have entered the following donor:");
			System.out.println(donor);
			
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
						
			valid = true;
			
		}
		return donor;
	}
	
	/**
	 * Prompts the user for information about creating a new address row/object.
	 * This method will continue to prompt the user until valid data is entered,
	 * e.g., valid numeric zipcode, valid state abbreviation. 
	 * 
	 * @return Address	an address object if the data was successfully validated. 
	 */
	private static Company promptAndCreateCompany(){
		boolean valid = false;
		Company company = null;
		
		outer:
		while(!valid){
			System.out.println("Enter the following information:");
			System.out.print("Name: > ");
			String name = sc.nextLine().trim();
			
			Address address = promptAndCreateAddress();
			
			company = new Company(name, address);
			
			System.out.println("You have entered the following company:");
			System.out.println(company);
			
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
						
			valid = true;
			
		}
		return company;
	}
	
	/**
	 * Prompts the user for information about creating a new address row/object.
	 * This method will continue to prompt the user until valid data is entered,
	 * e.g., valid numeric zipcode, valid state abbreviation. 
	 * 
	 * @return Address	an address object if the data was successfully validated. 
	 */
	private static Donation promptAndCreateDonation(){
		boolean valid = false;
		Donation donation = null;
		
		outer:
		while(!valid){
			System.out.println("Enter the following information:");
			
			boolean validAmountPledged = false;
			int amountPledged = 0;
			while(!validAmountPledged){
				System.out.print("Amount pledged: > ");
				String amountPledgedStr = sc.nextLine().trim().replaceAll("\\D", "");
				try{
					amountPledged = Integer.parseInt(amountPledgedStr);
				}catch(NumberFormatException e){
					System.err.println("Invalid amount: " + amountPledgedStr);
					continue;
				}
				validAmountPledged = true;
			}
			
			boolean validAmountDonated = false;
			int amountDonated = 0;
			while(!validAmountDonated){
				System.out.print("Amount donated: > ");
				String amountDonatedStr = sc.nextLine().trim().replaceAll("\\D", "");
				try{
					amountDonated = Integer.parseInt(amountDonatedStr);
				}catch(NumberFormatException e){
					System.err.println("Invalid amount: " + amountDonatedStr);
					continue;
				}
				validAmountDonated = true;
			}
			
			
			System.out.print("Payment method: > ");
			String paymentMethod = sc.nextLine().trim();
			
			
			boolean validDate = false;
			Date dateLastPayment = null;
			while(!validDate){
				System.out.print("Most recent payment date (MM/DD/YYYY): > ");
				SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
				sdf.setLenient(false);
				
				
				String dateStr = sc.nextLine().trim();
				java.util.Date utilDate = null;
				
				try{
					utilDate = sdf.parse(dateStr);
				}catch(ParseException e){
					System.err.println("Invalid date: " + sdf.format(utilDate));
					continue;
				}
				dateLastPayment = new Date(utilDate.getTime());
				validDate = true;
			}
			
			
			donation = new Donation(amountPledged, amountDonated, paymentMethod, dateLastPayment);
			
			System.out.println("You have entered the following donation:");
			System.out.println(donation);
			
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
						
			valid = true;
			
		}
		return donation;
	}
	
	private static Donated promptAndCreateDonated(int donationId, int donorId){
		
		Donated donated = null;
		
		boolean validDate = false;
		Date donationDate = null;
		while(!validDate){
			System.out.print("Enter donation date (MM/DD/YYYY): > ");
			SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
			sdf.setLenient(false);
			
			
			String dateStr = sc.nextLine().trim();
			java.util.Date utilDate = null;
			
			try{
				utilDate = sdf.parse(dateStr);
			}catch(ParseException e){
				System.err.println("Invalid date: " + sdf.format(utilDate));
				continue;
			}
			donationDate = new Date(utilDate.getTime());
			validDate = true;
		}
		
		donated = new Donated(donationId, donorId, donationDate);
		return donated;
	}
	
private static MatchingCorp promptAndCreateMatchingCorp(int donationId, int donorId){
		return new MatchingCorp(donationId, donorId);
	}
	
	private static String[] getCategories(){
		PreparedStatement stmt = null;
		String[] categories = null;
		
		try{
			if(c.isClosed())
				return null;
			/*
			String sql = "select count(type) from category;";
			stmt = c.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			stmt.close();
			
			int rowCount = 0;
			if(rs.next()){
				rowCount = rs.getInt(1);
			}
			rs.close();*/
			
			
			String sql = "select typeid, type from category;";
			stmt = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			
			int rowCount = 0;
			while(rs.next())
				rowCount++;
			
			rs.beforeFirst();
			
			categories = new String[rowCount];
			
			int i = 0;
			while(rs.next()){
				//Category category = Category.open(rs.getInt(1));
				categories[i] = rs.getString(1)+"~"+rs.getString(2);
				i++;
			}
			
			stmt.close();
			rs.close();
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return categories;
	}
	
	public static boolean promptYesOrNo(){
		boolean yes = false;
		boolean validResponse = false;
		while(!validResponse){
			String response = sc.nextLine().trim();
			if(response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n")){
				yes = false;
			}else if(response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")){
				yes = true;;
			}else{
				System.out.println("What? > ");
			}
		}
		
		return yes;
	}
	
	
}
