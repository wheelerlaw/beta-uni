import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Circle{
	public final int circleId;
	public final String circleName;
	public final int donationTier;
	
	private Circle(int Id, String circleName, int donationTier){
		this.circleId = Id;
		this.circleName = circleName;
		this.donationTier = donationTier;
	}
	
	
	public static Circle open(int circleId){
		Connection c = BetaUniversity.getConnection();
		PreparedStatement stmt = null;
		
		Circle circle = null;
		
		try {
			if(c.isClosed()){
				return null;
			}
			
			String sql = "select circleName, donationTier from circle where circleId = ?;";
			stmt = c.prepareStatement(sql);
			
			stmt.setInt(1, circleId);
			
			ResultSet rs = stmt.executeQuery();
			
			String circleName = null;
			int donationTier = 0; 
			if(rs.next()){
				circleName = rs.getString(1);
				donationTier = rs.getInt(2);
			}
			if(circleName == null){
				return null;
			}
			
			rs.close();
			stmt.close();
			
			circle = new Circle(circleId, circleName, donationTier);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return circle;
		
	}
	
	public String toString(){
		return this.circleName;
	}
	
}