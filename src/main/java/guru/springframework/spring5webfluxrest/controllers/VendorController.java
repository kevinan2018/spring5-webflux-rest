package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repository.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = VendorController.BASE_URL)
public class VendorController {
    public static final String BASE_URL = "/api/v1/vendors";
    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping
    Flux<Vendor> list() {
        return vendorRepository.findAll();
    }

    @GetMapping("{id}")
    Mono<Vendor> getById(@PathVariable String id) {
        return vendorRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    Mono<Void> createVendor(@RequestBody Publisher<Vendor> vendorStream) {
//        return vendorRepository.saveAll(vendorStream).then();
//    }
    Flux<Vendor> createVendor(@RequestBody Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream);
    }


//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    Mono<Vendor> create(@RequestBody Vendor vendor) {
//        return vendorRepository.save(vendor);
//    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Vendor> patch(@PathVariable String id, @RequestBody Vendor vendor) {
        return vendorRepository.findById(id)
                .flatMap(vend -> {
                    boolean changed = false;
                    if (vendor.getFistName() != null && !vendor.getFistName().equals(vend.getFistName())) {
                        vend.setFistName(vendor.getFistName());
                        changed = true;
                    }
                    if (vendor.getLastName() != null && !vendor.getLastName().equals(vend.getLastName())) {
                        vend.setLastName(vendor.getLastName());
                        changed = true;
                    }

                    if (changed) {
                        return vendorRepository.save(vend);
                    }
                    return Mono.just(vend);

                }).switchIfEmpty(Mono.error(new RuntimeException("Not Found id: " + id)));
    }

}
