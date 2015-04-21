package business.customersubsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import middleware.creditverifcation.CreditVerificationFacade;
import middleware.exceptions.DatabaseException;
import middleware.exceptions.MiddlewareException;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassAddressForTest;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;

public class CustomerSubsystemFacade implements CustomerSubsystem {
	private static final Logger LOG = Logger.getLogger(CustomerSubsystemFacade.class.getPackage().getName());
	
	ShoppingCartSubsystem shoppingCartSubsystem;
	OrderSubsystem orderSubsystem;
	List<Order> orderHistory;
	AddressImpl defaultShipAddress;
	AddressImpl defaultBillAddress;
	CreditCardImpl defaultPaymentInfo;
	CustomerProfileImpl customerProfile;
	
	DbClassAddress dbAddress;
	DbClassCreditCard dbCreditCard;
	CreditVerificationFacade creditVerification;
	
	/** Use for loading order history,
	 * default addresses, default payment info, 
	 * saved shopping cart,cust profile
	 * after login*/
    public void initializeCustomer(Integer id, int authorizationLevel) 
    		throws BackendException {
    	LOG.info("initializeCustomer, with id:"+id);
    	dbAddress=new DbClassAddress();    	
    	dbCreditCard=new DbClassCreditCard();
    	creditVerification=new CreditVerificationFacade();
    	
	    boolean isAdmin = (authorizationLevel >= 1);
		loadCustomerProfile(id, isAdmin);
		dbAddress.setCustomerProfile(customerProfile);
		dbCreditCard.setCustomerId(customerProfile.getCustId());
		
		loadDefaultShipAddress();
		loadDefaultBillAddress();
		loadDefaultPaymentInfo();
		shoppingCartSubsystem = ShoppingCartSubsystemFacade.INSTANCE;
		shoppingCartSubsystem.setCustomerProfile(customerProfile);
		shoppingCartSubsystem.retrieveSavedCart();
		loadOrderData();	
		
    }
    
    void loadCustomerProfile(int id, boolean isAdmin) throws BackendException {
    	LOG.info("loadCustomerProfile, with id:"+id);
    	try {
			DbClassCustomerProfile dbclass = new DbClassCustomerProfile();
			dbclass.readCustomerProfile(id);
			customerProfile = dbclass.getCustomerProfile();
			customerProfile.setIsAdmin(isAdmin);
			
			dbCreditCard.readDefaultPayment(id);
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}
    }
    void loadDefaultShipAddress() throws BackendException {
    	LOG.info("loadDefaultShipAddress");    	
    	defaultShipAddress= dbAddress.getDefaultShipAddress();
    }
	void loadDefaultBillAddress() throws BackendException {
		LOG.info("loadDefaultBillAddress");		
    	defaultBillAddress= dbAddress.getDefaultBillAddress();
	}
	void loadDefaultPaymentInfo() throws BackendException {	
		LOG.info("loadDefaultPaymentInfo");		
		defaultPaymentInfo=dbCreditCard.getDefaultPaymentInfo();
	}
	void loadOrderData() throws BackendException {

		// retrieve the order history for the customer and store here (Uncommented 2nd line - Tasid)
		orderSubsystem = new OrderSubsystemFacade(customerProfile);
		orderHistory = orderSubsystem.getOrderHistory();
	}
    /**
     * Returns true if user has admin access
     */
    public boolean isAdmin() {
    	return customerProfile.isAdmin();
    }
    
    
    
    /** 
     * Use for saving an address created by user  
     */
    public void saveNewAddress(Address addr) throws BackendException {
    	LOG.info("saveNewAddress:"+addr.toString());
    	try {
			DbClassAddress dbClass = new DbClassAddress();
			dbClass.setAddress(addr);
			dbClass.saveAddress(customerProfile);
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
    }
    
    public CustomerProfile getCustomerProfile() {
    	LOG.info("getCustomerProfile");
		return customerProfile;
	}

	public Address getDefaultShippingAddress() {
		LOG.info("getDefaultShippingAddress");
		return defaultShipAddress;
	}

	public Address getDefaultBillingAddress() {
		LOG.info("getDefaultBillingAddress");
		return defaultBillAddress;
	}
	public CreditCard getDefaultPaymentInfo() {
		LOG.info("getDefaultPaymentInfo");
		return defaultPaymentInfo;
	}
 
    
    /** 
     * Use to supply all stored addresses of a customer when he wishes to select an
	 * address in ship/bill window 
	 */
    public List<Address> getAllAddresses() throws BackendException {    	
    	LOG.info("getAllAddresses");
    	return dbAddress.getAddressList();
    }

	public Address runAddressRules(Address addr) throws RuleException,
			BusinessException {

		Rules transferObject = new RulesAddress(addr);
		transferObject.runRules();

		// updates are in the form of a List; 0th object is the necessary
		// Address
		AddressImpl update = (AddressImpl) transferObject.getUpdates().get(0);
		return update;
	}

	public void runPaymentRules(Address addr, CreditCard cc)
			throws RuleException, BusinessException {
		Rules transferObject = new RulesPayment(addr, cc);
		transferObject.runRules();
	}
	
	
	public static Address createAddress(String street, String city,
			String state, String zip, boolean isShip, boolean isBill) {
		return new AddressImpl(street, city, state, zip, isShip, isBill);
	}

	public static CustomerProfile createCustProfile(Integer custid,
			String firstName, String lastName, boolean isAdmin) {
		return new CustomerProfileImpl(custid, firstName, lastName, isAdmin);
	}

	public static CreditCard createCreditCard(String nameOnCard,
			String expirationDate, String cardNum, String cardType) {
		return new CreditCardImpl(nameOnCard, expirationDate, cardNum, cardType);
	}
	//Added - Tasid
	@Override
	public List<Order> getOrderHistory() {
		LOG.info("getOrderHistory");
		return orderHistory;
	}

	

	@Override
	public void setShippingAddressInCart(Address addr) {
		LOG.info("setShippingAddressInCart");
		
		shoppingCartSubsystem.getLiveCart().setShipAddress(addr);;
		
	}

	@Override
	public void setBillingAddressInCart(Address addr) {
		LOG.info("setBillingAddressInCart");
		shoppingCartSubsystem.getLiveCart().setBillAddress(addr);
		
	}

	@Override
	public void setPaymentInfoInCart(CreditCard cc) {
		LOG.info("setPaymentInfoInCart");
		shoppingCartSubsystem.getLiveCart().setPaymentInfo(cc);
		
	}

	@Override
	public void submitOrder() throws BackendException {
		LOG.info("submitOrder");
		ShoppingCart liveCart=shoppingCartSubsystem.getLiveCart();
		orderSubsystem.submitOrder(liveCart);
		
	}

	@Override
	public void refreshAfterSubmit() throws BackendException {
		shoppingCartSubsystem.clearLiveCart();
		
	}

	@Override
	public ShoppingCartSubsystem getShoppingCart() {
		LOG.info("getShoppingCart");
		return shoppingCartSubsystem;
		
	}

	@Override
	public void saveShoppingCart() throws BackendException {
		LOG.info("saveShoppingCart");
		shoppingCartSubsystem.saveLiveCart();
		
	}

	@Override
	public void checkCreditCard(CreditCard cc) throws BusinessException {
		LOG.info("checkCreditCard");
		try {
			creditVerification.checkCreditCard(customerProfile, defaultBillAddress, cc, 0);
		} catch (MiddlewareException e) {
			
			e.printStackTrace();
		}
		
	}

	@Override
	public DbClassAddressForTest getGenericDbClassAddress() {
		return new DbClassAddress();		
	}

	@Override
	public CustomerProfile getGenericCustomerProfile() {
		return new CustomerProfileImpl(1, "FirstTest", "LastTest");
	}

}
