package business.customersubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

public class DbClassCreditCard implements DbClass{
	private static final Logger LOG = 
			Logger.getLogger(DbClassCustomerProfile.class.getPackage().getName());
	private DataAccessSubsystem dataAccessSS = 
    	new DataAccessSubsystemFacade();
    private final String READ = "Read";
    private Integer custId;
    String query;
    private String queryType;
    private CreditCardImpl defaultCreditCard;
    
    public CreditCardImpl getDefaultPaymentInfo()
    {
    	return defaultCreditCard;
    }
    
    public void readDefaultPayment(Integer custId) throws DatabaseException {
        this.custId = custId;
        queryType=READ;
        dataAccessSS.atomicRead(this);      	
    }
	    
	@Override
	public void buildQuery() throws DatabaseException {
		LOG.info("Query for " + queryType + ": " + query);
        if(queryType.equals(READ)){
        	query = "select nameoncard,expdate,cardtype,cardnum  "+
	                "FROM Customer "+
	                "WHERE custid = "+custId;
        }
		
	}

	@Override
	public void populateEntity(ResultSet resultSet) throws DatabaseException {
		  try {
		        
	            //we take the first returned row
	            if(resultSet.next()){
	                defaultCreditCard = new CreditCardImpl(resultSet.getString("nameoncard"),
	                								resultSet.getString("expdate"),
	                								resultSet.getString("cardnum"),
	                                               resultSet.getString("cardtype"));
	            }
	        }
	        catch(SQLException e){
	            throw new DatabaseException(e);
	        }
		
	}

	 public String getDbUrl() {
	    	DbConfigProperties props = new DbConfigProperties();	
	    	return props.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
	 
	 }

	 
    public String getQuery() {
        return query;
 
    }


}
