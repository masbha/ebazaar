package business.ordersubsystem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.DbClassOrderForTest;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.ShoppingCart;

public class OrderSubsystemFacade implements OrderSubsystem {
	private static final Logger LOG = 
			Logger.getLogger(OrderSubsystemFacade.class.getPackage().getName());
	CustomerProfile custProfile;
	    
    public OrderSubsystemFacade(CustomerProfile custProfile){
        this.custProfile = custProfile;
    }
	
	
	
	/** Used whenever an order item needs to be created from outside the order subsystem */
    public static OrderItem createOrderItem(Integer prodId,Integer orderId, String quantityReq, String totalPrice) {
    	return null;
    }
    
    /** to create an Order object from outside the subsystem */    
    public static Order createOrder(Integer orderId, LocalDate orderDate, List<OrderItem> orderItems) {
    	OrderImpl order = new OrderImpl();
    	//autoboxing of Integer will throw an exception if orderId is null
    	if (orderId != null) order.setOrderId(orderId);
    	order.setDate(orderDate);
    	order.setOrderItems(orderItems);
    	return order;
    	
    }
    
    ///////////// Methods internal to the Order Subsystem -- NOT public
    List<Integer> getAllOrderIds() throws DatabaseException {
        
        DbClassOrder dbClass = new DbClassOrder();
        return dbClass.getAllOrderIds(custProfile);
        
    }
    List<OrderItem> getOrderItems(Integer orderId) throws DatabaseException {
        DbClassOrder dbClass = new DbClassOrder();
        return dbClass.getOrderItems(orderId);
    }
    
    OrderImpl getOrderData(Integer orderId) throws DatabaseException {
    	DbClassOrder dbClass = new DbClassOrder();
    	return dbClass.getOrderData(orderId);
    }



	@Override
	//Implemented - Tasid
	public List<Order> getOrderHistory() throws BackendException {
		// TODO Auto-generated method stub
		List<Order> orderHistory = new ArrayList<Order>();
		try{
			List<Integer> allOrderIds= getAllOrderIds();
			for(int orderId:allOrderIds){
				
				List<OrderItem> orderItems= getOrderItems(orderId);
				OrderImpl orderImpl = getOrderData(orderId);
				orderImpl.setOrderItems(orderItems);
				
				orderHistory.add(orderImpl);
			}
			
			
		}catch(DatabaseException de){
			throw new BackendException(de.getMessage());
		}
		
		return orderHistory;
	}



	@Override
	//Implemened - Tasid
	public void submitOrder(ShoppingCart shopCart) throws BackendException {
		// TODO Auto-generated method stub
		try{
			DbClassOrder dbClass = new DbClassOrder(new OrderImpl(),custProfile);
			dbClass.submitOrder(shopCart);
		}catch(DatabaseException de){
			throw new BackendException(de.getMessage());
		
		}
		
	}
	
	
	@Override
	//Only For Test Case
	//Implemened - Tasid
	public DbClassOrderForTest getGenericDbClassOrder() {
		// TODO Auto-generated method stub
		return new DbClassOrder();
	}
	
    
}
