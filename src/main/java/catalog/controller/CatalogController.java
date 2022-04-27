package catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import catalog.model.ProductBoundary;
import catalog.service.CatalogServiceImplementation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class CatalogController {

    private final CatalogServiceImplementation catalog;

    @Autowired
    public CatalogController(CatalogServiceImplementation catalog) {
        super();
        this.catalog = catalog;
    }

    @RequestMapping(path = "/catalog",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ProductBoundary> store(@RequestBody ProductBoundary newProduct) {
        return this.catalog
                .store(newProduct);
    }
    
    @RequestMapping(path = "/catalog/{productId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ProductBoundary> getById(@PathVariable("productId") String productId) {
        return this.catalog
                .getById(productId);
    }
    
    @RequestMapping(path = "/catalog",
            method = RequestMethod.DELETE)
    public Mono<Void> deleteAll() {
        return this.catalog
                .deleteAll();
    }
    
    @RequestMapping(path = "/catalog",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductBoundary> getAllFiltered(
            @RequestParam(required = false, defaultValue = "") String filterType,
            @RequestParam(required = false, defaultValue = "") String filterValue,
            @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false, defaultValue = "id") String sortBy) {
        return this.catalog
                .getAllFiltered(filterType, filterValue, sortOrder, sortBy);
    }
}