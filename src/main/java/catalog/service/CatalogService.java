package catalog.service;

import catalog.model.ProductBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogService {

    Mono<Void> deleteAll();

    Mono<ProductBoundary> store(ProductBoundary newProduct);

    Flux<ProductBoundary> getAllFiltered(String filterType, String filterValue, String sortOrder, String sortBy);

    Mono<ProductBoundary> getById(String productId);
}
