
package business.usecasecontrol;

import java.util.List;

import business.BusinessConstants;
import business.SessionCache;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Order;



/**
 * @author pcorazza
 */
public class ViewOrdersController   {
	
	//Added - Tasid
	public static List<Order> getOrderHistory(){
		
		CustomerSubsystem cust = (CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);
		return cust.getOrderHistory();
		
		
		
	}
	
	
}
