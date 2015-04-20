package business.shoppingcartsubsystem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.mysql.jdbc.log.Log;

import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;

public enum ShoppingCartSubsystemFacade implements ShoppingCartSubsystem {
	INSTANCE;
	
	private static final Logger LOG = Logger.getLogger(ShoppingCartSubsystemFacade.class.getPackage().getName());
	
	ShoppingCartImpl liveCart = new ShoppingCartImpl(new LinkedList<CartItem>());
	ShoppingCartImpl savedCart;
	Integer shopCartId;
	CustomerProfile customerProfile;
	Logger log = Logger.getLogger(this.getClass().getPackage().getName());

	// interface methods
	public void setCustomerProfile(CustomerProfile customerProfile) {
		this.customerProfile = customerProfile;
	}
	
	public void makeSavedCartLive() {
		liveCart = savedCart;
	}
	
	public ShoppingCart getLiveCart() {
		return liveCart;
	}
	

	public void retrieveSavedCart() throws BackendException {
		try {
			LOG.info("Trying to retrieve saved cart.");
			DbClassShoppingCart dbClass = new DbClassShoppingCart();
			ShoppingCartImpl cartFound = dbClass.retrieveSavedCart(customerProfile);
			if(cartFound == null) {
				savedCart = new ShoppingCartImpl(new ArrayList<CartItem>());
			} else {
				savedCart = cartFound;
			}
		} catch(DatabaseException e) {
			LOG.warning("Failed to retrieve saved cart.");
			throw new BackendException(e);
		}

	}
	
	public static CartItem createCartItem(String productName, String quantity,
            String totalprice) {
		try {
			LOG.info("Trying tor create cart time...");
			return new CartItemImpl(productName, quantity, totalprice);
		} catch(BackendException e) {
			LOG.warning("Failed to create cart item...");
			throw new RuntimeException("Can't create a cartitem because of productid lookup: " + e.getMessage());
		}
	}
	
	public void updateShoppingCartItems(List<CartItem> list) {
		liveCart.setCartItems(list);
	}
	
	public List<CartItem> getCartItems() {
		return liveCart.getCartItems();
	}


	
	//interface methods for testing
	
	public ShoppingCart getEmptyCartForTest() {
		return new ShoppingCartImpl();
	}

	
	public CartItem getEmptyCartItemForTest() {
		return new CartItemImpl();
	}

	@Override
	public void clearLiveCart() {
		liveCart.clearCart();
		
	}

	@Override
	public List<CartItem> getLiveCartItems() {
		return liveCart.getCartItems();
	}

	@Override
	public void setShippingAddress(Address addr) {
		liveCart.setShipAddress(addr);
		
	}

	@Override
	public void setBillingAddress(Address addr) {
		liveCart.setBillAddress(addr);
		
	}

	@Override
	public void setPaymentInfo(CreditCard cc) {
		liveCart.setPaymentInfo(cc);
		
	}

	@Override
	public void saveLiveCart() throws BackendException {
		DbClassShoppingCart dbClass = new DbClassShoppingCart();
		try {
			LOG.info("Trying to save live cart...");
			dbClass.saveCart(customerProfile, liveCart);
		} catch (DatabaseException e) {
			LOG.warning("Saving cart failed");
			throw new BackendException(e);
		}		
	}

	@Override
	public void runShoppingCartRules() throws RuleException, BusinessException {
		Rules transferObject = new RulesShoppingCart(liveCart);
		transferObject.runRules();
	}

	@Override
	public void runFinalOrderRules() throws RuleException, BusinessException {
		Rules transferObject = new RulesFinalOrder(liveCart);
		transferObject.runRules();
		
	}

}
