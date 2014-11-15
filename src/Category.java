import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class representing the category table in the database. 
 * @author Wheeler
 *
 */
public class Category{
	public final int typeId;
	public final String type;
	
	/**
	 * Private constructor for creating a object from an existing row. 
	 * @param typeId	The id of the type.
	 * @param type		The name of the type.
	 */
	private Category(int typeId, String type){
		this.typeId = typeId;
		this.type = type;
	}
	
	/**
	 * Opens an an exiting row of an object by taking in its id and creating an object 
	 * with the data from the row. 
	 * @param typeId	The id of the category. 
	 * @return Category	The category in object form. 
	 */
	public static Category open(int typeId){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		Category category = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select type from category where typeId = ?;";
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, typeId);
			
			ResultSet rs = stmt.executeQuery();
			
			String type = null;
			if(rs.next()){
				type = rs.getString(1);
			}
			if(type == null){
				return null;
			}
			
			rs.close();
			stmt.close();
			
			category = new Category(typeId, type);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return category;
		
	}
	
	public String toString(){
		return this.type;
	}
	
}