package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repository.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CategoryControllerTest {

    WebTestClient webTestClient;
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @Before
    public void setUp() throws Exception {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void list() {
        given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Cat1").build(),
                        Category.builder().description("Cat2").build()));

        webTestClient.get().uri(CategoryController.BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        given(categoryRepository.findById("someid"))
                .willReturn(Mono.just(Category.builder().description("Cat1").build()));

        webTestClient.get()
                .uri(CategoryController.BASE_URL+"/someid")
                .exchange()
                .expectBody(Category.class);
    }

    @Test
    public void create() {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().description("desc").build()));

        Mono<Category> catToSavedMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.post().uri(CategoryController.BASE_URL)
                .body(catToSavedMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void update() {
        Category cat = Category.builder().description("desc").build();
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(cat));

        Mono<Category> catToUpdatedMono = Mono.just(cat);

        webTestClient.put().uri(CategoryController.BASE_URL + "/id")
                .body(catToUpdatedMono, Category.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Category.class)
                .hasSize(1)
                .contains(cat);

    }

    @Test
    public void patchWithChange() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description("desc").build()));

        Category catRet = Category.builder().description("desc2").build();

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(catRet));

        Mono<Category> catToUpdatedMono = Mono.just(catRet);

        webTestClient.patch().uri(CategoryController.BASE_URL + "/id")
                .body(catToUpdatedMono, Category.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category.class)
                .isEqualTo(catRet);

        verify(categoryRepository).save(any());
        then(categoryRepository).should().save(any());
    }

    @Test
    public void patchNoChange() {
        Category cat = Category.builder().description("desc").build();

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(cat));

        Category catPatch = Category.builder().build();

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(catPatch));

        Mono<Category> catToUpdatedMono = Mono.just(catPatch);

        webTestClient.patch().uri(CategoryController.BASE_URL + "/id")
                .body(catToUpdatedMono, Category.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category.class)
                .isEqualTo(cat);

        verify(categoryRepository, never()).save(any());
        then(categoryRepository).should(never()).save(any());
    }

    @Test
    public void patchWithSameDesc() {
        Category cat = Category.builder().description("desc").build();

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(cat));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(cat));

        Mono<Category> catToUpdatedMono = Mono.just(cat);

        webTestClient.patch().uri(CategoryController.BASE_URL + "/id")
                .body(catToUpdatedMono, Category.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category.class)
                .isEqualTo(cat);

        verify(categoryRepository, never()).save(any());
        then(categoryRepository).should(never()).save(any());
    }
}