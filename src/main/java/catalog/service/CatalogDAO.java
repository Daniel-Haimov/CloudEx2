package catalog.service;


import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;

import catalog.model.ProductEntity;
import reactor.core.publisher.Flux;

public interface CatalogDAO extends ReactiveMongoRepository<ProductEntity, String> {

    Flux<ProductEntity> findAllByName(@Param("name") String name, Sort sort);

    Flux<ProductEntity> findAllByPriceGreaterThanEqual(@Param("price") double price, Sort sort);

    Flux<ProductEntity> findAllByPriceLessThanEqual(@Param("price") double price, Sort sort);

    Flux<ProductEntity> findAllByCategory(@Param("category") String category, Sort sort);
}
