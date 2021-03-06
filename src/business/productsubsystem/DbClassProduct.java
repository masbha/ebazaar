package business.productsubsystem;

import static business.util.StringParse.makeString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import presentation.gui.GuiUtils;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductFromGui;
import business.util.TwoKeyHashMap;

class DbClassProduct implements DbClass {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DbClassProduct.class
			.getPackage().getName());
	private DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();

	/**
	 * The productTable matches product ID with Product object. It is static so
	 * that requests for "read product" based on product ID can be handled
	 * without extra db hits
	 */
	private static TwoKeyHashMap<Integer, String, Product> productTable;
	private String queryType;
	private String query;
	private Product product;
	private String description;
	private List<Product> productList;
	Catalog catalog;
	Integer productId;

	private final String LOAD_PROD_TABLE = "LoadProdTable";
	private final String READ_PRODUCT = "ReadProduct";
	private final String READ_PROD_LIST = "ReadProdList";
	private final String SAVE_NEW_PROD = "SaveNewProd";

	public void buildQuery() {
		if (queryType.equals(LOAD_PROD_TABLE)) {
			buildProdTableQuery();
		} else if (queryType.equals(READ_PRODUCT)) {
			buildReadProductQuery();
		} else if (queryType.equals(READ_PROD_LIST)) {
			buildProdListQuery();
		} else if (queryType.equals(SAVE_NEW_PROD)) {
			buildSaveNewQuery();
		}

	}

	private void buildProdTableQuery() {
		query = "SELECT * FROM product";
	}

	private void buildProdListQuery() {
		query = "SELECT * FROM Product WHERE catalogid = " + catalog.getId();
	}

	private void buildReadProductQuery() {
		query = "SELECT * FROM Product WHERE productid = " + productId;
	}

	private void buildSaveNewQuery() {
		//implement
	}

	public TwoKeyHashMap<Integer, String, Product> readProductTable()
			throws DatabaseException {
		if (productTable != null) {
			return productTable.clone();
		}
		return refreshProductTable();
	}

	/**
	 * Force a database call
	 */
	public TwoKeyHashMap<Integer, String, Product> refreshProductTable()
			throws DatabaseException {
		queryType = LOAD_PROD_TABLE;
		dataAccessSS.atomicRead(this);
		
		// Return a clone since productTable must not be corrupted
		return productTable.clone();
	}

	public List<Product> readProductList(Catalog cat)
			throws DatabaseException {
		if (productList == null) {
			return refreshProductList(cat);
		}
		return Collections.unmodifiableList(productList);
	}

	public List<Product> refreshProductList(Catalog cat)
			throws DatabaseException {
		this.catalog = cat;
		queryType = READ_PROD_LIST;
		dataAccessSS.atomicRead(this);
		return productList;
	}

	public Product readProduct(Integer productId)
			throws DatabaseException {
		if (productTable != null && productTable.isAFirstKey(productId)) {
			return productTable.getValWithFirstKey(productId);
		}
		queryType = READ_PRODUCT;
		this.productId = productId;
		dataAccessSS.atomicRead(this);
		return product;
	}
	

	/**
	 * Database columns: productid, productname, totalquantity, priceperunit,
	 * mfgdate, catalogid, description
	 */
	public void saveNewProduct(ProductFromGui product, Integer catalogid,
			String description) throws DatabaseException {
		//implement
	}

	public void populateEntity(ResultSet resultSet) throws DatabaseException {
		if (queryType.equals(LOAD_PROD_TABLE)) {
			populateProdTable(resultSet);
		} else if (queryType.equals(READ_PRODUCT)) {
			populateProduct(resultSet);
		} else if (queryType.equals(READ_PROD_LIST)) {
			populateProdList(resultSet);
		}
	}
	
	

	private void populateProdList(ResultSet rs) throws DatabaseException {
		productList = new LinkedList<Product>();
		try {
			Product product = null;
			Integer prodId = null;
			String productName = null;
			Integer quantityAvail = null;
			Double unitPrice = null;
			String mfgDate = null;
			Integer catalogId = null;
			String description = null;
			while (rs.next()) {
				prodId = rs.getInt("productid");
				productName = rs.getString("productname");
				quantityAvail = rs.getInt("totalquantity");
				unitPrice =rs.getDouble("priceperunit");
				mfgDate = rs.getString("mfgdate");
				catalogId = rs.getInt("catalogid");
				description = rs.getString("description");
				CatalogImpl catalog = new CatalogImpl(catalogId, 
						(new CatalogTypesImpl()).getCatalogName(catalogId));
				//Catalog c, Integer pi, String pn, int qa, 
				//double up, LocalDate md, String d
				product = new ProductImpl(catalog, prodId, productName, quantityAvail,
						 unitPrice, GuiUtils.localDateForString(mfgDate),
					    description);
				productList.add(product);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Internal method to ensure that product table is up to date.
	 */
	private void populateProdTable(ResultSet rs) throws DatabaseException {
		productTable = new TwoKeyHashMap<Integer, String, Product>();
		try {
			Product product = null;
			Integer prodId = null;
			String productName = null;
			Integer quantityAvail = null;
			Double unitPrice = null;
			String mfgDate = null;
			Integer catalogId = null;
			String description = null;
			while (rs.next()) {
				prodId = rs.getInt("productid");
				productName = rs.getString("productname");
				quantityAvail = rs.getInt("totalquantity");
				unitPrice = rs.getDouble("priceperunit");
				mfgDate = rs.getString("mfgdate");
				catalogId = rs.getInt("catalogid");
				description = rs.getString("description");
				CatalogImpl catalog = new CatalogImpl(catalogId, 
						(new CatalogTypesImpl()).getCatalogName(catalogId));
				product = new ProductImpl(catalog, prodId, productName, quantityAvail,
						unitPrice, GuiUtils.localDateForString(mfgDate), description);
				productTable.put(prodId, productName, product);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	private void populateProduct(ResultSet rs) throws DatabaseException {
		try {
			if (rs.next()) {
				CatalogImpl catalog = new CatalogImpl(rs.getInt("catalogid"), 
						(new CatalogTypesImpl()).getCatalogName(rs.getInt("catalogid")));
				
				product = new ProductImpl(catalog, rs.getInt("productid"),
						rs.getString("productname"),
						rs.getInt("totalquantity"),
						rs.getDouble("priceperunit"),
						GuiUtils.localDateForString(rs.getString("mfgdate")),
						rs.getString("description"));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	public String getDbUrl() {
		DbConfigProperties props = new DbConfigProperties();
		return props.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
	}

	public String getQuery() {
		return query;
	}
}
