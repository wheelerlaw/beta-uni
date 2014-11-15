import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a single row in the donation table. The attributes
 * represent the columns in the table. 
 * @author Wheeler
 *
 */
public class MatchingCorp{
	public final int donationId;
	public final int corpId;
	
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
	public MatchingCorp(int donationId, int corpId){
		this.donationId = donationId;
		this.corpId = corpId;
		
		this.newObject = true;
	}
	
	/**
	 * Returns the Id of this object/row. Throws an exception if save() has not been called on this object. 
	 * @return int The id of the object/row. 
	 * @throws SQLNotSavedException Thrown if the object was not saved. 
	 */
	public int id() throws SQLNotSavedException{
		if(this.newObject){
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
		
		if(!this.newObject)
			return 1;
		
		try {
			if(c.isClosed()){
				return 1;
			}
			
			
			String sql = "insert into donate(donationId, corpId) values(?, ?);";
			
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, this.donationId);
			stmt.setInt(2, this.corpId);
			
			stmt.executeUpdate();
			
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 1;	
	}
	
	/**
	 * Opens an an existing row in the database and creates an object around it. 
	 * @param donorId	The id of the donor.
	 * @return Donor	The donor object, or null if there was an error. 
	 */
	public static MatchingCorp open(int donationId){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		MatchingCorp matchingCorp = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select corpId from matchingcorp where donationId = ?;";
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, donationId);
			
			ResultSet rs = stmt.executeQuery();
			
			Integer corpId = null;
			if(rs.next()){
				corpId = rs.getInt(1);
			}
			if(corpId == null)
				return null;
			
			rs.close();
			stmt.close();
			
			matchingCorp = new MatchingCorp(donationId, corpId);
			matchingCorp.newObject = false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return matchingCorp;
		
	}
	
	/**
	 * Returns a string representation of the donor. 
	 * @return String the string representation of the donor. 
	 */
	public String toString(){
		String string = "";
		
		string += "Donation ID:\t\t" + this.donationId + "\n";
		string += "Donor ID:\t\t" + this.corpId;
		
		return string;
	}
	
}