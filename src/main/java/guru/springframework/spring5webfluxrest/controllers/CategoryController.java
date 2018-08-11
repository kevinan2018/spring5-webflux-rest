package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repository.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = CategoryController.BASE_URL)
public class CategoryController {
    public static final String BASE_URL = "/api/v1/categories";

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    Flux<Category> list() {
        return categoryRepository.findAll();
    }

    @GetMapping("{id}")
    Mono<Category> getById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //Mono<Void> create(@RequestBody Publisher<Category> categoryStream) {
    //    return categoryRepository.saveAll(categoryStream).then();
    //}
    Flux<Category> create(@RequestBody Publisher<Category> categoryStream) {
        return categoryRepository.saveAll(categoryStream);
    }


    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Category> update(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Category> patch(@PathVariable String id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .flatMap(cat -> {
                    if (category.getDescription() != null && !category.getDescription().equals(cat.getDescription())) {
                        cat.setDescription(category.getDescription());
                        return categoryRepository.save(cat);
                    }
                    return Mono.just(cat);
                }).switchIfEmpty(Mono.error(new RuntimeException("Not Found id: " + id)));

    }
}
