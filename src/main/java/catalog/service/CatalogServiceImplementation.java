package catalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import catalog.exception.InvalidInputException;
import catalog.exception.ProductExistException;
import catalog.model.ProductBoundary;
import catalog.model.ProductEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CatalogServiceImplementation implements CatalogService {

    private final CatalogDAO catalog;
    private final ServiceUtil util;

    @Autowired
    public CatalogServiceImplementation(CatalogDAO catalog) {
        super();
        this.catalog = catalog;
        this.util = new ServiceUtil();
    }

    @Override
    public Mono<Void> deleteAll() {
        return this.catalog
                .deleteAll();
    }

    @Override
    public Mono<ProductBoundary> store(ProductBoundary newProduct) {
        if (newProduct.getProductId() == null)
        	return Mono.error(() -> new InvalidInputException("Product must have an ID."));

        return this.catalog.findById(newProduct.getProductId())
                .flatMap(productInDb -> Mono.error(() -> new ProductExistException("Product already exist with id: " + newProduct.getProductId())))
                .then(Mono.just(newProduct))
                .map(this.util::toEntity)
                .flatMap(this.catalog::save)
                .map(this.util::toBoundary)
                .log();
    }

    @Override
    public Mono<ProductBoundary> getById(String productId) {
        return this.catalog
                .findById(productId)
                .map(this.util::toBoundary)
                .log();
    }

    @Override
    public Flux<ProductBoundary> getAllFiltered(String filterType, 
    		String filterValue, 
    		String sortOrder, 
    		String sortBy, 
    		String page, 
    		String size, 
    		String minPrice, 
    		String maxPrice) {
        // Validate sortOrder is ASC/DESC only
        if (!sortOrder.isEmpty() && !sortOrder.equals("ASC") && !sortOrder.equals("DESC"))
            return Flux.error(() -> new InvalidInputException("Invalid sortOrder. Use ASC/DESC or none. Received: " + sortOrder));

        // Validate sortBy is productId/name/price/description/category/productDetails  only
        final String sortby = sortBy.toLowerCase();	
        if (!sortby.isEmpty() && !sortby.equals("productid") && !sortby.equals("name") && !sortby.equals("price")
        		&& !sortby.equals("category") && !sortby.equals("description") && !sortby.equals("productdetails")) {
        	
        	return Flux.error(() -> new InvalidInputException("Invalid sortBy. Use productId/name/price/description/category/productDetails or none. Received: " + sortby));
        }

        Flux<ProductEntity> rv;
        double minPriceDouble, maxPriceDouble;

        
     // Validate filterType is All/byName/byCategoryName/byPrice only
        switch (filterType.toLowerCase()) {
        	case "all":
                rv = this.catalog.findAll(Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            case "byname":
                rv = this.catalog.findAllByName(filterValue, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            case "bycategoryname":
            	rv = this.catalog.findAllByCategory(filterValue, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
            	break;
            case "byprice":
                try {
                	minPriceDouble = Double.parseDouble(minPrice);
                	maxPriceDouble = Double.parseDouble(maxPrice);
                } catch (Exception e) {
                    return Flux.error(() -> new InvalidInputException("Used byPrice but not provided numbers. Received: minPrice=" + minPrice + " maxPrice: " + maxPrice));
                }
                rv = this.catalog.findAllByPriceBetween(minPriceDouble, maxPriceDouble, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            default:
                return Flux.error(() -> new InvalidInputException("Invalid filterType. Use byName, byMinPrice, byMaxPrice, byCategoryName or empty. Received: " + filterType));
        }

        return rv
                .map(this.util::toBoundary)
                .log();
    }
}