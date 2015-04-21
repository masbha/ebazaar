package business.externalinterfaces;

import java.util.List;

import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

/* Used only for testing DbClassAddress */
public interface DbClassOrderForTest extends DbClass {	
	public List<Integer> readAllOrders(CustomerProfile custProfile) throws DatabaseException;
}