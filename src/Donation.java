import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

/**
 * This class represents a single row in the donation table. The attributes
 * represent the columns in the table. 
 * @author Wheeler
 *
 */
public class Donation{
	private Integer donationId;
	public int amountPledged;
	public int amountDonated;
	public String paymentMethod;
	public Date dateLastPayment;
	
	//This just determines whether to do an insert or an update. Not an actual column in the table.
	private boolean newObject;
	
	/**
	 * Create an instance of an object that represents an existing row in the database. 
	 * This constructor is private because the user will not know what the donorId is until
	 * the object is in the database. 
	 * This constructor is only used by the static open() method.
	 * 
	 * @param donorId		The id of the donor
	 * @param name			The name of the donor
	 * @param spouseName	The name of the spouse of the donor
	 * @param YOG			The year of graduation of the donor
	 * @param category		The id of the type of donor (student, alumni, etc.)
	 * @param address		The id of the address of the donor.
	 * @param circle		The id of the donor circle the donor belongs to. 
	 */
	private Donation(int donationId, int amountPledged, int amountDonated, String paymentMethod, Date dateLastPayment){
		this.donationId = donationId;
		this.amountPledged = amountPledged;
		this.amountDonated = amountDonated;
		this.paymentMethod = paymentMethod;
		this.dateLastPayment = dateLastPayment;
		
		this.newObject = false;
	}
	
	/**
	 * Public constructor for creating a new row in a table. This is public because this
	 * is how the user will create a new row in the table. 
	 * 
	 * @param name			The name of the donor
	 * @param spouseName	The name of the spouse of the donor
	 * @param YOG			The year of graduation of the donor
	 * @param category		The id of the type of donor (student, alumni, etc.)
	 * @param address		The id of the address of the donor.
	 * @param circle		The id of the donor circle the donor belongs to. 
	 */
	public Donation(int amountPledged, int amountDonated, String paymentMethod, Date dateLastPayment){
		this.donationId = null;
		this.amountPledged = amountPledged;
		this.amountDonated = amountDonated;
		this.paymentMethod = paymentMethod;
		this.dateLastPayment = dateLastPayment;
		
		this.newObject = true;
	}
	
	/**
	 * Returns the Id of this object/row. Throws an exception if save() has not been called on this object. 
	 * @return int The id of the object/row. 
	 * @throws SQLNotSavedException Thrown if the object was not saved. 
	 */
	public int id() throws SQLNotSavedException{
		if(this.donationId == null){
			throw new SQLNotSavedException("Donation not saved in database");
		}else{
			return this.donationId;
		}
	}
	
	/**
	 * Saves the object in the database. Determines whether to use an insert or an update statement. 
	 * @return int A status value if the save was successful or not. 0 if successful, >=1 otherwise. 
	 */
	public int save(){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		try {
			if(c.isClosed()){
				return 1;
			}
			
			String sql = "";
			if(newObject){
				sql = "insert into donation(amountPledged, amountDonated, paymentMethod, dateLastPayment) values(?, ?, ?, ?);";
			}else{
				sql = "update donor set amountPledged=?, amountDonated=?, paymentMethod=?, dateLastPayment=? where donationId=?;";
			}
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, this.amountPledged);
			stmt.setInt(2, this.amountDonated);
			stmt.setString(3, this.paymentMethod);
			stmt.setDate(4, this.dateLastPayment);
			
			if(!newObject){
				stmt.setInt(7, this.donationId);
			}
			
			stmt.executeUpdate();
			
			// need to get the ID that was created by the database. 
			if(newObject){
				sql = "select max(donationId) from donation where amountPledged=? and amountDonated=? and paymentMethod=? and dateLastPayment=?;";
				stmt = c.prepareStatement(sql);

				stmt.setInt(1, this.amountPledged);
				stmt.setInt(2, this.amountDonated);
				stmt.setString(3, this.paymentMethod);
				stmt.setDate(4, this.dateLastPayment);
				
				ResultSet rs = stmt.executeQuery();
				if(rs.next()){
					this.donationId = rs.getInt(1);
				}
			}
			
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 1;	
	}
	
	/**
	 * Opens an an existing row in the databse and creates an object around it. 
	 * @param donorId	The id of the donor.
	 * @return Donor	The donor object, or null if there was an error. 
	 */
	public static Donation open(int donationId){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		Donation donation = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select amountPledged, amountDonated, paymentMethod, dateLastPayment from donation where donationId = ?;";
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, donationId);
			
			ResultSet rs = stmt.executeQuery();
			
			int amountPledged = 0;
			int amountDonated = 0;
			String paymentMethod = null;
			Date dateLastPayment = null;
			if(rs.next()){
				amountPledged = rs.getInt(1);
				amountDonated = rs.getInt(2);
				paymentMethod = rs.getString(3);
				dateLastPayment = rs.getDate(4);
			}
			if(dateLastPayment == null)
				return null;
			
			rs.close();
			stmt.close();
			
			donation = new Donation(donationId, amountPledged, amountDonated, paymentMethod, dateLastPayment);
			donation.newObject = false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return donation;
		
	}
	
	/**
	 * Returns a string representation of the donor. 
	 * @return String the string representation of the donor. 
	 */
	public String toString(){
		String string = "";
		
		string += "ID:\t\t\t" + this.donationId + "\n";
		string += "Pledged:\t\t" + this.amountPledged + "\n";
		string += "Donated:\t\t" + this.amountDonated + "\n";
		string += "Payment Method:\t\t" + this.paymentMethod + "\n";
		string += "Date Last Payment:\t" + this.dateLastPayment + "\n";
		return string;
	}
	
}