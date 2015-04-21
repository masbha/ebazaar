package business.externalinterfaces;

import java.util.List;

import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

public interface DbClassShoppingCartForTest extends DbClass{
	public ShoppingCart readSavedCart(CustomerProfile customerProfile) throws DatabaseException;
}
