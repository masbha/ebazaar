package presentation.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import presentation.gui.GuiUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import business.CartItemData;
import business.Util;
import business.exceptions.BackendException;
import business.externalinterfaces.*;
import business.productsubsystem.ProductSubsystemFacade;
import business.usecasecontrol.BrowseAndSelectController;
import business.usecasecontrol.ManageProductsController;

public enum ManageProductsData {
	INSTANCE;
	
	private CatalogPres defaultCatalog = readDefaultCatalogFromDataSource();
	private CatalogPres readDefaultCatalogFromDataSource() {
		ManageProductsController mpc = new  ManageProductsController();
		//TODO:need to refactor
		try {
			return mpc.getCatalogs().stream()
				.map(catalog -> Util.catalogToCatalogPres(catalog))
				.collect(Collectors.toList()).get(0);
		} catch (BackendException be) {
			return new CatalogPres();
		}
		//return DefaultData.CATALOG_LIST_DATA.get(0);
	}
	public CatalogPres getDefaultCatalog() {
		return defaultCatalog;
	}
	
	private CatalogPres selectedCatalog = defaultCatalog;
	public void setSelectedCatalog(CatalogPres selCatalog) {
		selectedCatalog = selCatalog;
	}
	public CatalogPres getSelectedCatalog() {
		return selectedCatalog;
	}
	//////////// Products List model
	private ObservableMap<CatalogPres, List<ProductPres>> productsMap
	   = readProductsFromDataSource();
	
	/** Initializes the productsMap */
	private ObservableMap<CatalogPres, List<ProductPres>> readProductsFromDataSource() {
		//TODO:need to refactor
		ManageProductsController mpc = new  ManageProductsController();
		List<CatalogPres> catalogListPress = new  ArrayList<CatalogPres>();
		ObservableMap<CatalogPres, List<ProductPres>> productListData = FXCollections.observableHashMap();
		try {
			catalogListPress = mpc.getCatalogs().stream()
				    .map(catalog -> Util.catalogToCatalogPres(catalog))
				    .collect(Collectors.toList());
			for (CatalogPres catalogPress : catalogListPress) {
				productListData.put(catalogPress, mpc.getProductsList(catalogPress.getCatalog().getName())
						.stream()
						.map(prod -> Util.productToProductPres(prod))
						.collect(Collectors.toList()));
			}
						
		} catch (BackendException e) {
			//TODO:
		}
		return productListData;
		//return DefaultData.PRODUCT_LIST_DATA;
	}
	
	
	public void updateProductsMap(CatalogPres catPres) {
		//TODO:need to refactor
		ManageProductsController mpc = new  ManageProductsController();
		
		try {
			productsMap.put(catPres, mpc.getProductsList(catPres.getCatalog().getName())
					.stream()
					.map(prod -> Util.productToProductPres(prod))
					.collect(Collectors.toList()));	
		} catch (BackendException e) {
			//TODO:
		}
		
	}
	/** Delivers the requested products list to the UI */
	public ObservableList<ProductPres> getProductsList(CatalogPres catPres) {
		return FXCollections.observableList(productsMap.get(catPres));
	}
	
	public ProductPres productPresFromData(Catalog c, String name, String date,  //MM/dd/yyyy 
			int numAvail, double price) {
		
		Product product = ProductSubsystemFacade.createProduct(c, name, 
				GuiUtils.localDateForString(date), numAvail, price);
		ProductPres prodPres = new ProductPres();
		prodPres.setProduct(product);
		return prodPres;
	}
	
	public void addToProdList(CatalogPres catPres, ProductPres prodPres) {
		ObservableList<ProductPres> newProducts =
		           FXCollections.observableArrayList(prodPres);
		List<ProductPres> specifiedProds = productsMap.get(catPres);
		
		//Place the new item at the bottom of the list
		specifiedProds.addAll(newProducts);
	}
	
	/** This method looks for the 0th element of the toBeRemoved list 
	 *  and if found, removes it. In this app, removing more than one product at a time
	 *  is  not supported.
	 */
	public boolean removeFromProductList(CatalogPres cat, ObservableList<ProductPres> toBeRemoved) {
		if(toBeRemoved != null && !toBeRemoved.isEmpty()) {
			ManageProductsController mpc = new ManageProductsController();
			try {
				mpc.deleteProduct(toBeRemoved.get(0).getProduct());
				boolean result = productsMap.get(cat).remove(toBeRemoved.get(0));
				return result;
			} catch (BackendException e) {
				return false;
			}
			
		}
		return false;
	}
		
	//////// Catalogs List model
	private ObservableList<CatalogPres> catalogList = readCatalogsFromDataSource();

	/** Initializes the catalogList */
	private ObservableList<CatalogPres> readCatalogsFromDataSource() {
		ManageProductsController mpc = new  ManageProductsController();
		List<CatalogPres> catalogListPress = new  ArrayList<CatalogPres>();
		
		try {
			catalogListPress = mpc.getCatalogs().stream()
				    .map(catalog -> Util.catalogToCatalogPres(catalog))
				    .collect(Collectors.toList());
						
		} catch (BackendException e) {
			//TODO:
		}

		return FXCollections.observableList(catalogListPress);
	}

	/** Delivers the already-populated catalogList to the UI */
	public ObservableList<CatalogPres> getCatalogList() {
		//TODO: will be implemented different way
		catalogList = readCatalogsFromDataSource();
		return catalogList;
	}

	public CatalogPres catalogPresFromData(int id, String name) {
		Catalog cat = ProductSubsystemFacade.createCatalog(id, name);
		CatalogPres catPres = new CatalogPres();
		catPres.setCatalog(cat);
		return catPres;
	}

	public boolean addToCatalogList(CatalogPres catPres) {
		ObservableList<CatalogPres> newCatalogs = FXCollections
				.observableArrayList(catPres);
		boolean result = false;
		ManageProductsController mpc = new ManageProductsController();
		try {
			mpc.saveCatalog(catPres.getCatalog());
			// Place the new item at the bottom of the list
			// catalogList is guaranteed to be non-null
			result = catalogList.addAll(newCatalogs);
			if (result) { // must make this catalog accessible in productsMap
				productsMap.put(catPres, FXCollections
						.observableList(new ArrayList<ProductPres>()));
			}
		} catch (BackendException e) {
			//System.out.println("This is message::::: " + e.getMessage());					
		}
		return result;
	}

	/**
	 * This method looks for the 0th element of the toBeRemoved list in
	 * catalogList and if found, removes it. In this app, removing more than one
	 * catalog at a time is not supported.
	 * 
	 * This method also updates the productList by removing the products that
	 * belong to the Catalog that is being removed.
	 * 
	 * Also: If the removed catalog was being stored as the selectedCatalog,
	 * the next item in the catalog list is set as "selected"
	 */
	public boolean removeFromCatalogList(ObservableList<CatalogPres> toBeRemoved) {
		boolean result = false;
		CatalogPres item = toBeRemoved.get(0);
		
		ManageProductsController mpc = new ManageProductsController();
		try {
			mpc.deleteCatalog(item.getCatalog());
		} catch (BackendException e) {
			return result;
		}
		if (toBeRemoved != null && !toBeRemoved.isEmpty()) {
			result = catalogList.remove(item);
		}
		if(item.equals(selectedCatalog)) {
			if(!catalogList.isEmpty()) {
				selectedCatalog = catalogList.get(0);
			} else {
				selectedCatalog = null;
			}
		}
		if(result) {//update productsMap
			productsMap.remove(item);
		}
		return result;
	}
	
	//Synchronizers
	public class ManageProductsSynchronizer implements Synchronizer {
		@SuppressWarnings("rawtypes")
		@Override
		public void refresh(ObservableList list) {
			productsMap.put(selectedCatalog, list);
		}
	}
	public ManageProductsSynchronizer getManageProductsSynchronizer() {
		return new ManageProductsSynchronizer();
	}
	
	private class ManageCatalogsSynchronizer implements Synchronizer {
		@SuppressWarnings("rawtypes")
		@Override
		public void refresh(ObservableList list) {
			catalogList = list;
		}
	}
	public ManageCatalogsSynchronizer getManageCatalogsSynchronizer() {
		return new ManageCatalogsSynchronizer();
	}
}
