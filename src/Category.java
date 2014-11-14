import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Category{
	public final int typeId;
	public final String type;
	
	private Category(int typeId, String type){
		this.typeId = typeId;
		this.type = type;
	}
	
	
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
			
			rs.close();
			stmt.close();
			
			category = new Category(typeId, type);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return category;
		
	}
	
}