package presentation.control;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import presentation.data.CatalogPres;
import presentation.data.DefaultData;
import presentation.data.ManageProductsData;
import presentation.data.ProductPres;
import presentation.gui.AddCatalogPopup;
import presentation.gui.AddProductPopup;
import presentation.gui.MaintainCatalogsWindow;
import presentation.gui.MaintainProductsWindow;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.productsubsystem.ProductSubsystemFacade;
import business.usecasecontrol.ManageProductsController;


public enum ManageProductsUIControl {
	INSTANCE;

	private Stage primaryStage;
	private Callback startScreenCallback;
	private ManageProductsController mpc = new ManageProductsController();

	public void setPrimaryStage(Stage ps, Callback returnMessage) {
		primaryStage = ps;
		startScreenCallback = returnMessage;
	}

	// windows managed by this class
	MaintainCatalogsWindow maintainCatalogsWindow;
	MaintainProductsWindow maintainProductsWindow;
	AddCatalogPopup addCatalogPopup;
	AddProductPopup addProductPopup;

	// Manage catalogs
	private class MaintainCatalogsHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			maintainCatalogsWindow = new MaintainCatalogsWindow(primaryStage);
			ObservableList<CatalogPres> list = ManageProductsData.INSTANCE.getCatalogList();
			maintainCatalogsWindow.setData(list);
			maintainCatalogsWindow.show();
			primaryStage.hide();

		}
	}
	
	public MaintainCatalogsHandler getMaintainCatalogsHandler() {
		return new MaintainCatalogsHandler();
	}
	
	private class MaintainProductsHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			maintainProductsWindow = new MaintainProductsWindow(primaryStage);
			CatalogPres selectedCatalog = ManageProductsData.INSTANCE.getSelectedCatalog();
			if(selectedCatalog != null) {
				ObservableList<ProductPres> list = ManageProductsData.INSTANCE.getProductsList(selectedCatalog);
				maintainProductsWindow.setData(ManageProductsData.INSTANCE.getCatalogList(), list);
			}
			maintainProductsWindow.show();  
	        primaryStage.hide();
		}
	}
	public MaintainProductsHandler getMaintainProductsHandler() {
		return new MaintainProductsHandler();
	}
	
	private class BackButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {		
			maintainCatalogsWindow.clearMessages();		
			maintainCatalogsWindow.hide();
			startScreenCallback.clearMessages();
			primaryStage.show();
		}
			
	}
	public BackButtonHandler getBackButtonHandler() {
		return new BackButtonHandler();
	}
	
	private class BackFromProdsButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {		
			maintainProductsWindow.clearMessages();		
			maintainProductsWindow.hide();
			startScreenCallback.clearMessages();
			primaryStage.show();
		}
			
	}
	public BackFromProdsButtonHandler getBackFromProdsButtonHandler() {
		return new BackFromProdsButtonHandler();
	}
	
	private class AddCatalogsHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {		
			if (addCatalogPopup.getId().trim().equals("")) {
				addCatalogPopup.setMessageBar("ID field must be nonempty! \n[Type '0' to auto-generate ID.]");
			}
			else if (addCatalogPopup.getName().trim().equals("")) {  
				addCatalogPopup.setMessageBar("Name field must be nonempty!");
			}
			else {
				String idNewVal = addCatalogPopup.getId();
				if (idNewVal.equals("0")) {
					idNewVal = DefaultData.generateId(10);
				}
				try {
					Catalog newCat = mpc.createCatalog(Integer.parseInt(idNewVal), addCatalogPopup.getName());
					mpc.saveCatalog(newCat);
					CatalogPres catPres = new CatalogPres();
					catPres.setCatalog(newCat);
					maintainCatalogsWindow.addItem(catPres);
					addCatalogPopup.setMessageBar("");
					addCatalogPopup.hide();
				} catch (BackendException be) {
					addCatalogPopup.setMessageBar("Catalog saving fails");
					System.out.println("Catalog save fail : " + be.getMessage());
				}
				
			}	   
		}
	}
	
	public AddCatalogsHandler getAddCatalogsHandler() {
		return new AddCatalogsHandler();
	}
	
	private class AddProductPopupHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {		
			//Rules should be managed in a more maintainable way
			if(addProductPopup.getId().trim().equals("")) {
				addProductPopup.setMessageBar("Product Id field must be nonempty! \n[Type '0' to auto-generate ID.]");
			}
			else if(addProductPopup.getName().trim().equals("")) addProductPopup.setMessageBar("Product Name field must be nonempty!");
			else if(addProductPopup.getManufactureDate().trim().equals("")) addProductPopup.setMessageBar("Manufacture Date field must be nonempty!");
			else if(addProductPopup.getNumAvail().trim().equals("")) addProductPopup.setMessageBar("Number in Stock field must be nonempty!");
			else if(addProductPopup.getUnitPrice().trim().equals("")) addProductPopup.setMessageBar("Unit Price field must be nonempty!");
			else if(addProductPopup.getDescription().trim().equals("")) addProductPopup.setMessageBar("Description field must be nonempty!");
			else {
				Product newProd = null;
				String idNewVal = addProductPopup.getId();
				if(idNewVal.equals("0")) {
					idNewVal = DefaultData.generateId(100);
				} //Catalog c, Integer pi, String pn, int qa, double up, LocalDate md, String d
				try {
					Catalog catalog = mpc.getCatalog(addProductPopup.getCatalogName());
					newProd = mpc.createProduct(catalog, 
							Integer.parseInt(addProductPopup.getId()), addProductPopup.getName(), Integer.parseInt(addProductPopup.getNumAvail()), 
							    Double.parseDouble(addProductPopup.getUnitPrice()), LocalDate.parse(addProductPopup.getManufactureDate(), DateTimeFormatter.ofPattern("MM/dd/yyyy")), 
							    addProductPopup.getDescription());
					
					mpc.saveProduct(newProd);
					
				} catch (BusinessException be) {
					//TODO:
				}
				
				ProductPres prodPres = new ProductPres();
				prodPres.setProduct(newProd);
				maintainProductsWindow.addItem(prodPres);
				addProductPopup.setMessageBar("");
				addProductPopup.hide();
			}	  	   
		}
	}
	
	public AddProductPopupHandler getAddProductPopupHandler() {
		return new AddProductPopupHandler();
	}
	
	public void setAddCatalogWindowInfo(AddCatalogPopup catPopup) {
		this.addCatalogPopup = catPopup;		 
	}
	
	public void setAddProductWindowInfo(AddProductPopup productPopup) {
		this.addProductPopup = productPopup;		
	}

	/*
	 * private MenuItem maintainCatalogs() { MenuItem retval = new
	 * MenuItem("Maintain Catalogs"); retval.setOnAction(evt -> {
	 * MaintainCatalogsWindow maintain = new
	 * MaintainCatalogsWindow(primaryStage); ObservableList<CatalogPres> list =
	 * FXCollections.observableList( DefaultData.CATALOG_LIST_DATA);
	 * maintain.setData(list); maintain.show(); primaryStage.hide();
	 * 
	 * }); return retval; } private MenuItem maintainProducts() { MenuItem
	 * retval = new MenuItem("Maintain Products"); retval.setOnAction(evt -> {
	 * MaintainProductsWindow maintain = new
	 * MaintainProductsWindow(primaryStage); ObservableList<Product> list =
	 * FXCollections.observableList(
	 * DefaultData.PRODUCT_LIST_DATA.get(DefaultData.BOOKS_CATALOG));
	 * maintain.setData(DefaultData.CATALOG_LIST_DATA, list); maintain.show();
	 * primaryStage.hide();
	 * 
	 * }); return retval; }
	 */
}
