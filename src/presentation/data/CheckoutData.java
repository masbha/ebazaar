package presentation.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.usecasecontrol.BrowseAndSelectController;
import business.usecasecontrol.CheckoutController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.gui.GuiConstants;

public enum CheckoutData {
	INSTANCE;
	
	public Address createAddress(String street, String city, String state,
			String zip, boolean isShip, boolean isBill) {
		return CustomerSubsystemFacade.createAddress(street, city, state, zip, isShip, isBill);
	}
	
	public CreditCard createCreditCard(String nameOnCard,String expirationDate,
               String cardNum, String cardType) {
		return CustomerSubsystemFacade.createCreditCard(nameOnCard, expirationDate, 
				cardNum, cardType);
	}
	
	//Customer Ship Address Data
	private ObservableList<CustomerPres> shipAddresses = loadShipAddresses();
	
	//Customer Bill Address Data
	private ObservableList<CustomerPres> billAddresses = loadBillAddresses();
	
	private ObservableList<CustomerPres> loadShipAddresses() {		
	    List<CustomerPres> list = DefaultData.CUSTS_ON_FILE
						   .stream()
						   .filter(cust -> cust.getAddress().isShippingAddress())
						   .collect(Collectors.toList());
		return FXCollections.observableList(list);				   
										   
	}
	private ObservableList<CustomerPres> loadBillAddresses() {
		List list = DefaultData.CUSTS_ON_FILE
				   .stream()
				   .filter(cust -> cust.getAddress().isBillingAddress())
				   .collect(Collectors.toList());
		return FXCollections.observableList(list);
	}
	public ObservableList<CustomerPres> getCustomerShipAddresses() {
		return shipAddresses;
	}
	public ObservableList<CustomerPres> getCustomerBillAddresses() {
		return billAddresses;
	}
	public List<String> getDisplayAddressFields() {
		return GuiConstants.DISPLAY_ADDRESS_FIELDS;
	}
	public List<String> getDisplayCredCardFields() {
		return GuiConstants.DISPLAY_CREDIT_CARD_FIELDS;
	}
	public List<String> getCredCardTypes() {
		return GuiConstants.CREDIT_CARD_TYPES;
	}
	public Address getDefaultShippingData() {
		return CheckoutController.INSTANCE.getDefaultShippingAddress();
	}
	
	public Address getDefaultBillingData() {
		return CheckoutController.INSTANCE.getDefaultBillingAddress();
	}
	
	public CreditCard getDefaultPaymentInfo() {
		return CheckoutController.INSTANCE.getDefaultPaymentInfo();
	}
	
	
	public CustomerProfile getCustomerProfile() {
		return BrowseAndSelectController.INSTANCE.getCustomerProfile();
	}
	
		
	
	private class ShipAddressSynchronizer implements Synchronizer {
		public void refresh(ObservableList list) {
			shipAddresses = list;
		}
	}
	public ShipAddressSynchronizer getShipAddressSynchronizer() {
		return new ShipAddressSynchronizer();
	}
	
	private class BillAddressSynchronizer implements Synchronizer {
		public void refresh(ObservableList list) {
			billAddresses = list;
		}
	}
	public BillAddressSynchronizer getBillAddressSynchronizer() {
		return new BillAddressSynchronizer();
	}
	
	public static class ShipBill {
		public boolean isShipping;
		public String label;
		public Synchronizer synch;
		public ShipBill(boolean shipOrBill, String label, Synchronizer synch) {
			this.isShipping = shipOrBill;
			this.label = label;
			this.synch = synch;
		}
		
	}
	
}
