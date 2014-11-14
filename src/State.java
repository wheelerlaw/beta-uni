import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents one of the 50 U.S. states in the database in the state table. 
 * @author Wheeler
 *
 */
public class State{
	public final String stateAbbr;
	public final String stateName;
	
	/**
	 * Constructor for creating a new object from an existing row. 
	 * @param abbr	The state abbreviation.
	 * @param name	The state name.
	 */
	private State(String abbr, String name){
		this.stateAbbr = abbr;
		this.stateName = name;
	}
	
	/**
	 * Opens an existing row in the databse and creates and returns and object form it.
	 * @param stateAbbr	The state abbreviation (the primary key)
	 * @return State	The state, or null of there were errors.
	 */
	public static State open(String stateAbbr){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		stateAbbr = stateAbbr.toUpperCase();
		
		State state = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select stateName from state where stateAbbr = ?";
			stmt = c.prepareStatement(sql);
			
			stmt.setString(1, stateAbbr);
			
			ResultSet rs = stmt.executeQuery();
			
			String stateName = null;
			if(rs.next()){
				stateName = rs.getString(1);
			}
			if(stateName == null){
				return null;
			}
			
			rs.close();
			stmt.close();
			
			state = new State(stateAbbr, stateName);
			
		} catch (SQLException e) {
			return null;
		}
		
		return state;
		
	}
	
	/**
	 * Returns a string representation of this state in the form of the abbreviation.
	 * @return String The state abbreviation.
	 */
	@Override
	public String toString(){
		return this.stateAbbr;
	}
}