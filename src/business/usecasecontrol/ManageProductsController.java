
package business.usecasecontrol;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import presentation.data.CatalogPres;
import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;


public class ManageProductsController   {
    
    private static final Logger LOG = 
    	Logger.getLogger(ManageProductsController.class.getName());
    
    public List<Product> getProductsList(String catalog) throws BackendException {
    	//IMPLEMENT
    	ProductSubsystem pss = new ProductSubsystemFacade();
    	return pss.getProductList(pss.getCatalogFromName(catalog));
    }
    
    
    public void deleteProduct(Product product) throws BackendException {
    	//implement
    	ProductSubsystem pss = new ProductSubsystemFacade();
    	pss.deleteProduct(product);
    }
    
    public void deleteCatalog(Catalog c) throws BackendException {
    	//implement
    	ProductSubsystem pss = new ProductSubsystemFacade();
    	pss.deleteCatalog(c);
    	
    }
    
    public void saveCatalog(Catalog c) throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();
		pss.saveNewCatalog(c);
    	
    }
    
    public Catalog createCatalog(int id, String name) {
    	return ProductSubsystemFacade.createCatalog(id, name);
    }
    
    public List<Catalog> getCatalogs() throws BackendException {
		ProductSubsystem pss = new ProductSubsystemFacade();
		return pss.getCatalogList();
	}
    
    public Catalog getCatalog(String catName) throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();
    	return pss.getCatalogFromName(catName);
    }
    
    public Product createProduct(Catalog c, Integer pi, String pn, int qa, 
			double up, LocalDate md, String desc) {    	
    	return ProductSubsystemFacade.createProduct(c, pi, pn, qa, up, md, desc);
    }
    
    public void saveProduct(Product product) throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();
		pss.saveNewProduct(product);
    	
    }
    
}
