package presentation.control;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import presentation.data.CatalogPres;
import presentation.data.DataUtil;
import presentation.data.DefaultData;
import presentation.data.ManageProductsData;
import presentation.data.ProductPres;
import presentation.gui.AddCatalogPopup;
import presentation.gui.AddProductPopup;
import presentation.gui.MaintainCatalogsWindow;
import presentation.gui.MaintainProductsWindow;
import presentation.gui.TableUtil;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.UnauthorizedException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
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
	private class MaintainCatalogsHandler implements EventHandler<ActionEvent>, Callback {
		public void doUpdate() {
			if (DataUtil.custIsAdmin()) {
				try {
		    		Authorization.checkAuthorization(maintainCatalogsWindow, DataUtil.custIsAdmin());
		    		
		    	} catch(UnauthorizedException ue) {
		        	displayError(ue.getMessage());
		        	return;
		        }			
				ObservableList<CatalogPres> list = ManageProductsData.INSTANCE.getCatalogList();
				maintainCatalogsWindow.setData(list);
				maintainCatalogsWindow.show();
				primaryStage.hide();
			} else {
				primaryStage.show();
				startScreenCallback.displayError("You are not authorized to access this list");
			}
		}
		public Text getMessageBar() {
			return startScreenCallback.getMessageBar();
		}
		@Override
		public void handle(ActionEvent e) {
			maintainCatalogsWindow = new MaintainCatalogsWindow(primaryStage);
			boolean isLoggedIn = DataUtil.isLoggedIn();
			if (!isLoggedIn) {
				LoginUIControl loginControl = new LoginUIControl(maintainCatalogsWindow,
						primaryStage, this);
				loginControl.startLogin();
			} else {
				doUpdate();
			}
			
		}
	}
	
	public MaintainCatalogsHandler getMaintainCatalogsHandler() {
		return new MaintainCatalogsHandler();
	}
	
	private class MaintainProductsHandler implements EventHandler<ActionEvent>, Callback {
		public void doUpdate() {
			if (DataUtil.custIsAdmin()) {
				try {
		    		Authorization.checkAuthorization(maintainProductsWindow, DataUtil.custIsAdmin());
		    		
		    	} catch(UnauthorizedException ue) {
		        	displayError(ue.getMessage());
		        	return;
		        }
				
				CatalogPres selectedCatalog = ManageProductsData.INSTANCE.getSelectedCatalog();
				if(selectedCatalog != null) {
					ObservableList<ProductPres> list = ManageProductsData.INSTANCE.getProductsList(selectedCatalog);
					maintainProductsWindow.setData(ManageProductsData.INSTANCE.getCatalogList(), list);
				}
				maintainProductsWindow.show();  
		        primaryStage.hide();
			} else {
				primaryStage.show();
				startScreenCallback.displayError("You are not authorized to access this list");
			}
		}
		
		public Text getMessageBar() {
			return startScreenCallback.getMessageBar();
		}
		
		@Override
		public void handle(ActionEvent e) {
			maintainProductsWindow = new MaintainProductsWindow(primaryStage);
			boolean isLoggedIn = DataUtil.isLoggedIn();
			if (!isLoggedIn) {
				LoginUIControl loginControl = new LoginUIControl(maintainProductsWindow,
						primaryStage, this);
				loginControl.startLogin();
			} else {
				doUpdate();
			}
			
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
	//DELETE CATALOG
	private class DeleteCatalogButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {		
			TableUtil.selectByRow(maintainCatalogsWindow.getTable());
		    ObservableList<CatalogPres> tableItems = maintainCatalogsWindow.getTable().getItems();
		    ObservableList<Integer> selectedIndices = maintainCatalogsWindow.getTable().getSelectionModel().getSelectedIndices();
		    ObservableList<CatalogPres> selectedItems = maintainCatalogsWindow.getTable().getSelectionModel()
					.getSelectedItems();
		    if(tableItems.isEmpty()) {
		    	maintainCatalogsWindow.setMessageBar("Nothing to delete!");
		    } else if (selectedIndices == null || selectedIndices.isEmpty()) {
		    	maintainCatalogsWindow.setMessageBar("Please select a row.");
		    } else {
		    	boolean result =  ManageProductsData.INSTANCE.removeFromCatalogList(selectedItems);
			    if (result) {
			    	maintainCatalogsWindow.getTable().setItems(ManageProductsData.INSTANCE.getCatalogList());
			    	maintainCatalogsWindow.clearMessages();
			    } else {
			    	maintainCatalogsWindow.displayInfo("No items deleted.");
			    }
		    }
		}
			
	}
	public DeleteCatalogButtonHandler getDeleteCatalogButtonHandler() {
		return new DeleteCatalogButtonHandler();
	}
	//DELETE PRODUCT
	private class DeleteProductButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {		
			TableUtil.selectByRow(maintainProductsWindow.getTable());
			CatalogPres selectedCatalog = ManageProductsData.INSTANCE.getSelectedCatalog();
		    ObservableList<ProductPres> tableItems = ManageProductsData.INSTANCE.getProductsList(selectedCatalog);
		    ObservableList<Integer> selectedIndices = maintainProductsWindow.getTable().getSelectionModel().getSelectedIndices();
		    ObservableList<ProductPres> selectedItems = maintainProductsWindow.getTable().getSelectionModel()
					.getSelectedItems();
		    if(tableItems.isEmpty()) {
		    	maintainProductsWindow.setMessageBar("Nothing to delete!");
		    } else if (selectedIndices == null || selectedIndices.isEmpty()) {
		    	maintainProductsWindow.setMessageBar("Please select a row.");
		    } else {
		    	boolean result =  ManageProductsData.INSTANCE.removeFromProductList(selectedCatalog, selectedItems);
			    if (result) {
			    	maintainProductsWindow.getTable().setItems(ManageProductsData.INSTANCE.getProductsList(selectedCatalog));
			    	maintainProductsWindow.clearMessages();
			    } else {
			    	maintainProductsWindow.displayInfo("No items deleted.");
			    }
				
		    }
		}
			
	}
	public DeleteProductButtonHandler getDeleteProductButtonHandler() {
		return new DeleteProductButtonHandler();
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
//				if (idNewVal.equals("0")) {
//					idNewVal = DefaultData.generateId(10);
//				}
				Catalog newCat = mpc.createCatalog(Integer.parseInt(idNewVal), addCatalogPopup.getName());					
				//mpc.saveCatalog(newCat);
				CatalogPres catPres = new CatalogPres();
				catPres.setCatalog(newCat);
				if (maintainCatalogsWindow.addItem(catPres)) {
					addCatalogPopup.setMessageBar("");
					addCatalogPopup.hide();
				} else {
					addCatalogPopup.setMessageBar("Catalog saving fails");
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
					ProductPres prodPres = new ProductPres();
					prodPres.setProduct(newProd);
					maintainProductsWindow.addItem(prodPres);
					addProductPopup.setMessageBar("");
					addProductPopup.hide();
					
				} catch (BusinessException be) {
					//TODO:
					addProductPopup.setMessageBar("Product saving fails");
				}				
				
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
	
	public void setMaintainCatalogWindowInfo(MaintainCatalogsWindow catalogWindow) {
		this.maintainCatalogsWindow = catalogWindow;		
	}
	
	public void setMaintainProductWindowInfo(MaintainProductsWindow productWindow) {
		this.maintainProductsWindow = productWindow;		
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
