import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a single row in the company table. The attributes
 * represent the columns in the table. 
 * @author Wheeler
 *
 */
public class Company{
	private Integer corpId;
	public String name;
	public Address address;
	
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
	private Company(int corpId, String name, Address address){
		this.corpId = corpId;
		this.name = name;
		this.address = address;
		
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
	public Company(String name, Address address){
		this.corpId = null;
		this.name = name;
		this.address = address;
		
		this.newObject = true;
	}
	
	/**
	 * Returns the Id of this object/row. Throws an exception if save() has not been called on this object. 
	 * @return int The id of the object/row. 
	 * @throws SQLNotSavedException Thrown if the object was not saved. 
	 */
	public int id() throws SQLNotSavedException{
		if(this.corpId == null){
			throw new SQLNotSavedException("Donor not saved in database");
		}else{
			return this.corpId;
		}
	}
	
	/**
	 * Saves the object in the database. Determines whether to use an insert or an update statement. 
	 * @return int A status value if the save was successful or not. 0 if successful, >=1 otherwise. 
	 */
	public int save(){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		if(address.save() == 1){
			return 1;
		}
		
		try {
			if(c.isClosed()){
				return 1;
			}
			
			String sql = "";
			if(newObject){
				sql = "insert into company(name, address) values(?, ?);";
			}else{
				sql = "update donor set name=?, address=? where corpId=?;";
			}
			stmt = c.prepareStatement(sql);
			
			stmt.setString(1, name);
			stmt.setInt(2, address.id());
			
			if(!newObject){
				stmt.setInt(3, corpId);
			}
			
			stmt.executeUpdate();
			
			// need to get the ID that was created by the database. 
			if(newObject){
				sql = "select max(corpId) from company where name=? and address=?;";
				stmt = c.prepareStatement(sql);

				stmt.setString(1, name);
				stmt.setInt(2, address.id());
				
				ResultSet rs = stmt.executeQuery();
				if(rs.next()){
					this.corpId = rs.getInt(1);
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
	public static Company open(int corpId){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		Company company = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select name, address from company where corpId = ?;";
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, corpId);
			
			ResultSet rs = stmt.executeQuery();
			
			String name = null;
			int addressId = 0;
			if(rs.next()){
				name = rs.getString(1);
				addressId = rs.getInt(2);
			}
			
			rs.close();
			stmt.close();
			
			Address address = Address.open(addressId);
			company = new Company(corpId, name, address);
			company.newObject = false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return company;
		
	}
	
	/**
	 * Returns a string representation of the donor. 
	 * @return String the string representation of the donor. 
	 */
	public String toString(){
		String string = "";
		
		string += "ID:\t\t" + this.corpId + "\n";
		string += "Name:\t\t" + this.name + "\n";
		string += "Address:\n";
		string += address;
		return string;
	}
	
}