package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repository.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {
    //@Mock
    private VendorRepository vendorRepository;
    private VendorController vendorController;
    private WebTestClient webTestClient;

    @Before
    public void setUp() throws Exception {
        //MockitoAnnotations.initMocks(this);
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().fistName("vendor1").lastName("vendor1").build(),
                    Vendor.builder().fistName("vendor2").lastName("vendor2").build()
                ));
        webTestClient.get()
                .uri(VendorController.BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        given(vendorRepository.findById("someid"))
                .willReturn(Mono.just(Vendor.builder().fistName("vendor1").build()));

        webTestClient.get()
                .uri(VendorController.BASE_URL + "/someid")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class);
    }

    @Test
    public void createVendor() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSavedMono = Mono.just(Vendor.builder().fistName("vendor1").lastName("vendor1").build());

        webTestClient.post().uri(VendorController.BASE_URL)
                .body(vendorToSavedMono, Vendor.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(Vendor.class)
                .hasSize(1);
    }

//    @Test
//    public void create() {
//        Vendor vend = Vendor.builder().fistName("vendor1").lastName("vendor1").build();
//        given(vendorRepository.save(any(Vendor.class)))
//                .willReturn(Mono.just(vend));
//
//        Mono<Vendor> vendorToSavedMono = Mono.just(Vendor.builder().fistName("vendor1").lastName("vendor1").build());
//
//        webTestClient.post().uri(VendorController.BASE_URL)
//                .body(vendorToSavedMono,Vendor.class)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(Vendor.class)
//                .isEqualTo(vend);
//    }

    @Test
    public void update() {
        Vendor vendorUpdated = Vendor.builder().fistName("vendor1").lastName("vendor1").build();

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(vendorUpdated));

        Mono<Vendor> vendorToSavedMono = Mono.just(vendorUpdated);

        webTestClient.put().uri(VendorController.BASE_URL + "/id")
                .body(vendorToSavedMono, Vendor.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class)
                .isEqualTo(vendorUpdated);
    }

    @Test
    public void patchWithChange() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        Vendor vendorUpdated = Vendor.builder().fistName("vendor2").lastName("vendor2").build();

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(vendorUpdated));

        Mono<Vendor> vendorToSavedMono = Mono.just(vendorUpdated);

        webTestClient.patch().uri(VendorController.BASE_URL + "/id")
                .body(vendorToSavedMono, Vendor.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class)
                .isEqualTo(vendorUpdated);

        verify(vendorRepository).save(any());
        then(vendorRepository).should().save(any());
    }

    @Test
    public void patchNotChange() {
        Vendor vendor = Vendor.builder().fistName("vendor").lastName("vendor").build();

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(vendor));

        Vendor vendEmpty = Vendor.builder().build();

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(vendEmpty));

        Mono<Vendor> vendorMono = Mono.just(vendEmpty);

        webTestClient.patch().uri(VendorController.BASE_URL + "/id")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);

        verify(vendorRepository, never()).save(any());
        then(vendorRepository).should(never()).save(any());
    }

    @Test
    public void patchWithSameName() {
        Vendor vendor = Vendor.builder().fistName("vendor").lastName("vendor").build();

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(vendor));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(vendor));

        Mono<Vendor> vendorMono = Mono.just(vendor);

        webTestClient.patch().uri(VendorController.BASE_URL + "/id")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);

        verify(vendorRepository, never()).save(any());
        then(vendorRepository).should(never()).save(any());
    }
}