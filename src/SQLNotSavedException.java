import java.sql.SQLException;

public class SQLNotSavedException extends SQLException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4217011000737628546L;
	
	public SQLNotSavedException(String reason){
		super(reason);
		
	}
	
}