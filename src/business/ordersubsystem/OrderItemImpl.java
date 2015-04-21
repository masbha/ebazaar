package business.ordersubsystem;

import business.exceptions.BackendException;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;


public class OrderItemImpl implements OrderItem {
	private int orderItemId;
	private int orderId;
	private String productName;
	private int productId;
	private int quantity;
	private double unitPrice;
	public OrderItemImpl(String name, int quantity, double price) {
		productName = name;
		this.quantity = quantity;
		this.unitPrice = price;
	}
	
	 /** This version of constructor used when reading from database */
	//Implemented - Tasid
	public OrderItemImpl(int productId, int quantity) {
		this.quantity = quantity;
		ProductSubsystem prodSS= new ProductSubsystemFacade();
		try{
	        productName = prodSS.getProductFromId(productId).getProductName();
	        unitPrice = prodSS.getProductFromId(productId).getUnitPrice();
		}catch(BackendException e){
			System.out.println(e.getMessage());
		}
	}
	
	public int getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(int itemID) {
		this.orderItemId = itemID;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderID) {
		this.orderId = orderID;
	}


	public String getProductName() {
		return productName;
	}
	public void setProductName(String n) {
		productName = n;
	}


	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int q) {
		quantity = q;
	}


	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double price) {
		unitPrice = price;
	}

	public double getTotalPrice() {
		return unitPrice * quantity;
	}


	@Override
	//Fixes patch 6 - Tasid
	public int getProductId() {
		return productId;
	}

	@Override
	public void setProductId(int id) {
		productId = id;
		
	}

	

}
