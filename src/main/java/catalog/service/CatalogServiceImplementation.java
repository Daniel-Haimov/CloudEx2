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
    public Flux<ProductBoundary> getAllFiltered(String filterType, String filterValue, String sortOrder, String sortBy) {
        // Validate sortOrder is ASC/DESC only
        if (!sortOrder.isEmpty() && !sortOrder.equals("ASC") && !sortOrder.equals("DESC"))
            return Flux.error(() -> new InvalidInputException("Invalid sortOrder. Use ASC/DESC or none. Received: " + sortOrder));

        // Validate sortBy is email/name/birthdate/roles
        sortBy = sortBy.toLowerCase();
        if (!sortBy.isEmpty() && !sortBy.equals("id") && !sortBy.equals("name") && !sortBy.equals("price")
                && !sortBy.equals("category") && !sortBy.equals("image") && !sortBy.equals("productdetails"))
            return Flux.error(() -> new InvalidInputException("Invalid sortBy. Use id/name/price/category/image/productdetails or none. Received: " + sortOrder));

        Flux<ProductEntity> rv;
        double price;

        switch (filterType.toLowerCase()) {
            case "":
                rv = this.catalog
                        .findAll(Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            case "byname":
                rv = this.catalog
                        .findAllByName(filterValue, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            case "byminprice":
                try {
                    price = Double.parseDouble(filterValue);
                } catch (Exception e) {
                    return Flux.error(() -> new InvalidInputException("Used byMinPrice but not provided number. Received: " + filterValue));
                }
                rv = this.catalog
                        .findAllByPriceGreaterThanEqual(price, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            case "bymaxprice":
                try {
                    price = Double.parseDouble(filterValue);
                } catch (Exception e) {
                    return Flux.error(() -> new InvalidInputException("Used byMaxPrice but not provided number. Received: " + filterValue));
                }
                rv = this.catalog
                        .findAllByPriceLessThanEqual(price, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            case "bycategoryname":
                rv = this.catalog
                        .findAllByCategory(filterValue, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
                break;
            default:
                return Flux.error(() -> new InvalidInputException("Invalid filterType. Use byName, byMinPrice, byMaxPrice, byCategoryName or empty. Received: " + filterType));
        }

        return rv
                .map(this.util::toBoundary)
                .log();
    }
}