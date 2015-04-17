
package business.usecasecontrol;

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
    
    
    public void deleteProduct() {
    	//implement
    }
    
    public void saveCatalog(Catalog c) throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();
		pss.saveNewCatalog(c);
    	
    }
    
    public Catalog createCatalog(int id, String name) {
    	return ProductSubsystemFacade.createCatalog(id, name);
    }
    
    public List<Catalog> getCatalogList() throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();    	
    	return pss.getCatalogList();
    }
    
}
