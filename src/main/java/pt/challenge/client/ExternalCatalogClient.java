package pt.challenge.client;

import pt.challenge.dto.external.product.ProductCatalogDto;


/**
 * Interface defining the contract for the Product Catalog Client.
 * <p>
 * This client is responsible for interacting with the external product catalog
 * to fetch item details based on categories.
 * </p>
 */
public interface ExternalCatalogClient {

  /**
   * Fetches products belonging to a specific category.
   *
   * @param category the name of the product category
   * @return a DTO containing the category name and the list of associated products
   */
  ProductCatalogDto getProductsByCategory(String category);

  /**
   * Fallback method used when the product catalog service is unavailable or failing.
   *
   * @param category the name of the product category
   * @param t the exception that triggered the fallback
   * @return a default or cached product catalog response
   */
  ProductCatalogDto getProductsByCategoryFallback(String category, Throwable t);

}
