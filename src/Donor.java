import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a single row in the donor table. The attributes
 * represent the columns in the table. 
 * @author Wheeler
 *
 */
public class Donor{
	private Integer donorId;
	public String name;
	public String spouseName;
	public int YOG;
	public Category category;
	public Address address;
	public Circle circle;
	
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
	private Donor(int donorId, String name, String spouseName, int YOG, Category category, Address address, Circle circle){
		this.donorId = donorId;
		this.name = name;
		this.spouseName = spouseName;
		this.YOG = YOG;
		this.category = category;
		this.address = address;
		this.circle = circle;
		
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
	public Donor(String name, String spouseName, int YOG, Category category, Address address, Circle circle){
		this.donorId = null;
		this.name = name;
		this.spouseName = spouseName;
		this.YOG = YOG;
		this.category = category;
		this.address = address;
		this.circle = circle;
		
		this.newObject = true;
	}
	
	/**
	 * Returns the Id of this object/row. Throws an exception if save() has not been called on this object. 
	 * @return int The id of the object/row. 
	 * @throws SQLNotSavedException Thrown if the object was not saved. 
	 */
	public int id() throws SQLNotSavedException{
		if(this.donorId == null){
			throw new SQLNotSavedException("Donor not saved in database");
		}else{
			return this.donorId;
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
				sql = "insert into donor(name, yog, spouseName, address, category, circle) values(?, ?, ?, ?, ?, ?);";
			}else{
				sql = "update donor set name=?, yog=?, spouseName=?, address=?, category=?, circle=? where donorId=?;";
			}
			stmt = c.prepareStatement(sql);
			
			stmt.setString(1, name);
			stmt.setInt(2, YOG);
			stmt.setString(3, spouseName);
			stmt.setInt(4, address.id());
			stmt.setInt(5, category.typeId);
			stmt.setInt(6, circle.circleId);
			
			if(!newObject){
				stmt.setInt(7, donorId);
			}
			
			stmt.executeUpdate();
			
			// need to get the ID that was created by the database. 
			if(newObject){
				sql = "select max(donorId) from donor where name=?, yog=?, spouseName=?, address=?, category=?, circle=?;";
				stmt = c.prepareStatement(sql);

				stmt.setString(1, name);
				stmt.setInt(2, YOG);
				stmt.setString(3, spouseName);
				stmt.setInt(4, address.id());
				stmt.setInt(5, category.typeId);
				stmt.setInt(6, circle.circleId);
				
				ResultSet rs = stmt.executeQuery();
				if(rs.next()){
					this.donorId = rs.getInt(1);
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
	public static Donor open(int donorId){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		Donor donor = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select name, spouseName, YOG, category, address, circle from donor where donorId = ?;";
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, donorId);
			
			ResultSet rs = stmt.executeQuery();
			
			String name = null;
			String spouseName = null;
			int YOG = 0;
			int categoryId = 0;
			int addressId = 0;
			int circleId = 0;
			if(rs.next()){
				name = rs.getString(1);
				spouseName = rs.getString(2);
				YOG = rs.getInt(3);
				categoryId = rs.getInt(4);
				addressId = rs.getInt(5);
				circleId = rs.getInt(6);
			}
			
			rs.close();
			stmt.close();
			
			Category category = Category.open(categoryId);
			Address address = Address.open(addressId);
			Circle circle = Circle.open(circleId);
			donor = new Donor(donorId, name, spouseName, YOG, category, address, circle);
			donor.newObject = false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return donor;
		
	}
	
	/**
	 * Returns a string representation of the donor. 
	 * @return String the string representation of the donor. 
	 */
	public String toString(){
		String string = "";
		return string;
	}
	
}