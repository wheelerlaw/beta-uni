import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class that represents the address table in the database. 
 * @author Wheeler
 *
 */
public class Address{
	
	private Integer addrId;
	public String city;
	public String street;
	public State state;
	public int zipcode;
	
	// Not a row in the table, used to determine whether to use insert or update to save the object. 
	private boolean newObject;
	
	/**
	 * Private constructor used to recreate a row as an object. 
	 * @param addrId	The id of the address
	 * @param city		The city of the address
	 * @param street	The number and street of the address
	 * @param state		The state of the address.
	 * @param zipcode	The zipcode of the address.
	 */
	private Address(int addrId, String city, String street, State state, int zipcode){
		this.addrId = addrId;
		this.city = city;
		this.street = street;
		this.state = state;
		this.zipcode = zipcode;
		
		this.newObject = false;
	}
	
	/**
	 * Public constructor for creating a new row as an object. 
	 * @param street
	 * @param city
	 * @param state
	 * @param zipcode
	 * @throws IllegalArgumentException
	 */
	public Address(String street, String city, State state, int zipcode){
		this.addrId = null;
		this.city = city;
		this.street = street;
		this.state = state;
		this.zipcode = zipcode;
		
		this.newObject = true;
	}
	
	public int id() throws SQLNotSavedException{
		if(this.addrId == null){
			throw new SQLNotSavedException("Address not saved in database");
		}else{
			return this.addrId;
		}
	}
	
	public int save(){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		try {
			if(c.isClosed()){
				System.err.println("Connection closed.");
				return 1;
			}
			
			if(State.open(state.stateAbbr) == null){
				System.err.println("Invalid state: "+state.stateAbbr);
				return 1;
			}
			
			String sql = "insert into address(city, street, state, zipcode) values(?, ?, ?, ?);";
			stmt = c.prepareStatement(sql);
			
			stmt.setString(1, city);
			stmt.setString(2, street);
			stmt.setString(3, state.stateAbbr);
			stmt.setInt(4, zipcode);
			
			stmt.executeUpdate();
			
			return 0;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		
		return 1;	
	}
	
	public static Address open(int addrId){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		Address address = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select city, street, state, zipcode from address where addrid = ?;";
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, addrId);
			
			ResultSet rs = stmt.executeQuery();
			
			String city = null;
			String street = null;
			String stateStr = null;
			Integer zipcode = 0;
			if(rs.next()){
				city = rs.getString(1);
				street = rs.getString(2);
				stateStr = rs.getString(3);
				zipcode = rs.getInt(4);
			}
			
			rs.close();
			stmt.close();
			
			State state = State.open(stateStr);
			
			address = new Address(addrId, city, street, state, zipcode);
			
			
		} catch (SQLException e) {}
		
		return address;
		
	}
	
	public String toString(){
		return this.street + "\n" + this.city + ", " + this.state + " " + this.zipcode;
	}
}