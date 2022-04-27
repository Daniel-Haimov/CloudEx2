package catalog.service;

import catalog.model.ProductBoundary;
import catalog.model.ProductEntity;

public class ServiceUtil {

    public ServiceUtil() {
        super();
    }

    public ProductEntity toEntity(ProductBoundary boundary) {
        ProductEntity entity = new ProductEntity();
        entity.setProductId(boundary.getProductId());
        entity.setName(boundary.getName());
        entity.setPrice(boundary.getPrice());
        entity.setDescription(boundary.getDescription());
        entity.setCategory(boundary.getCategory());
        entity.setProductDetails(boundary.getProductDetails());
        return entity;
    }

    public ProductBoundary toBoundary(ProductEntity entity) {
        ProductBoundary boundary = new ProductBoundary();
        boundary.setProductId(entity.getProductId());
        boundary.setName(entity.getName());
        boundary.setPrice(entity.getPrice());
        boundary.setDescription(entity.getDescription());
        boundary.setCategory(entity.getCategory());
        boundary.setProductDetails(entity.getProductDetails());
        return boundary;
    }
}