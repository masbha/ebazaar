package business.productsubsystem;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import business.externalinterfaces.*;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.util.TwoKeyHashMap;

public class ProductSubsystemFacade implements ProductSubsystem {
	private static final Logger LOG = Logger.getLogger(ShoppingCartSubsystemFacade.class.getPackage().getName());
	public static Catalog createCatalog(int id, String name) {
		return new CatalogImpl(id, name);
	}
	public static Product createProduct(Catalog c, String name, 
			LocalDate date, int numAvail, double price) {
		return new ProductImpl(c, name, date, numAvail, price);
	}
	public static Product createProduct(Catalog c, Integer pi, String pn, int qa, 
			double up, LocalDate md, String desc) {
		return new ProductImpl(c, pi, pn, qa, up, md, desc);
	}
	
	/** obtains product for a given product name */
    public Product getProductFromName(String prodName) throws BackendException {
    	try {
			DbClassProduct dbclass = new DbClassProduct();
			return dbclass.readProduct(getProductIdFromName(prodName));
		} catch(DatabaseException e) {
			LOG.warning("Failed to load product from productname");
			throw new BackendException(e);
		}	
    }
    public Integer getProductIdFromName(String prodName) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			TwoKeyHashMap<Integer,String,Product> table = dbclass.readProductTable();
			return table.getFirstKey(prodName);
		} catch(DatabaseException e) {
			LOG.warning("Failed to load productid from productname");
			throw new BackendException(e);
		}
		
	}
    public Product getProductFromId(Integer prodId) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			return dbclass.readProduct(prodId);
		} catch(DatabaseException e) {
			LOG.warning("Failed to load product from productid");
			throw new BackendException(e);
		}
	}
    
    public List<Catalog> getCatalogList() throws BackendException {
    	try {
			DbClassCatalogTypes dbClass = new DbClassCatalogTypes();
			return dbClass.getCatalogTypes().getCatalogs();
		} catch(DatabaseException e) {
			LOG.warning("Failed to load catalog list");
			throw new BackendException(e);
		}
		
    }
    
    public List<Product> getProductList(Catalog catalog) throws BackendException {
    	try {
    		DbClassProduct dbclass = new DbClassProduct();
    		return dbclass.readProductList(catalog);
    	} catch(DatabaseException e) {
    		throw new BackendException(e);
    	}
    }
    
    @Override
	public int readQuantityAvailable(Product product) {
		//IMPLEMENT
		return product.getQuantityAvail();
	}
	
	@Override
	public Catalog getCatalogFromName(String catName) throws BackendException {
		try {
			DbClassCatalogTypes dbClass = new DbClassCatalogTypes();
			int id = dbClass.getCatalogTypes().getCatalogId(catName);
			return ProductSubsystemFacade.createCatalog(id, catName);
		} catch(DatabaseException e) {
			LOG.warning("Failed to load catalog from catalog name");
			throw new BackendException(e);
		}
		
	}
	
	@Override
	public void saveNewCatalog(Catalog catalog) throws BackendException {
		try {
			LOG.info("Trying to save new catalog");
			DbClassCatalog dbclass = new DbClassCatalog();
			int catalogId = dbclass.saveNewCatalog(catalog);
			catalog.setId(catalogId);
			
		}  catch(DatabaseException e) {
			LOG.warning("Failed to save new catalog");
    		throw new BackendException(e);
    	}
		
	}
	
	@Override
	public void saveNewProduct(Product product) throws BackendException {
		try {
			LOG.info("Trying to save new product");
			DbClassProduct dbclass = new DbClassProduct();
			dbclass.saveNewProduct(product);
		}  catch(DatabaseException e) {
			LOG.warning("Failed to save new product");
    		throw new BackendException(e);
    	}
		
	}
	
	@Override
	public void deleteProduct(Product product) throws BackendException {
		try {
			LOG.info("Trying to delete product");
			DbClassProduct dbclass = new DbClassProduct();
			dbclass.deleteProduct(product);
		}  catch(DatabaseException e) {
			LOG.warning("Failed to delete product");
    		throw new BackendException(e);
    	}		
	}
	
	@Override
	public void deleteCatalog(Catalog catalog) throws BackendException {
		try {
			LOG.info("Trying to delete catalog");
			DbClassCatalog dbclass = new DbClassCatalog();
			dbclass.deleteCatalog(catalog.getId());
		}  catch(DatabaseException e) {
			LOG.warning("Failed to delete catalog");
    		throw new BackendException(e);
    	}
		
	}
	
	@Override
	public DbClassCatalogTypes getGenericDbClassCatalogTypes() {
		return new DbClassCatalogTypes();
	}

}
