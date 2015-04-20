package presentation.data;

import java.util.List;
import business.usecasecontrol.ViewOrdersController;
import presentation.gui.GuiUtils;

public enum ViewOrdersData {
	INSTANCE;
	private OrderPres selectedOrder;
	public OrderPres getSelectedOrder() {
		return selectedOrder;
	}
	public void setSelectedOrder(OrderPres so) {
		selectedOrder = so;
	}
	
	public List<OrderPres> getOrders() {
		//Updated - Tasid
		return GuiUtils.orderListToOrderPresList(ViewOrdersController.getOrderHistory());
//		return DefaultData.ALL_ORDERS;
	}
}
