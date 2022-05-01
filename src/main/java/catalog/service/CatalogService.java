package catalog.service;

import catalog.model.ProductBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogService {

    Mono<Void> deleteAll();

    Mono<ProductBoundary> store(ProductBoundary newProduct);

    Flux<ProductBoundary> getAllFiltered(String filterType, String filterValue, String sortOrder, String sortBy, String page, String size, String minPrice, String maxPrice);

    Mono<ProductBoundary> getById(String productId);
}
