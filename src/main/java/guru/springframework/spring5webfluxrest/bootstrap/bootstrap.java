package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repository.CategoryRepository;
import guru.springframework.spring5webfluxrest.repository.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class bootstrap implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    public bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count().block() == 0) {
            //load data
            System.out.println("### LOADING DATA ON BOOTSTRAP ###");

            categoryRepository.save(Category.builder().description("Fruits").build()).block();
            categoryRepository.save(Category.builder().description("Nuts").build()).block();
            categoryRepository.save(Category.builder().description("Breads").build()).block();
            categoryRepository.save(Category.builder().description("Meats").build()).block();
            categoryRepository.save(Category.builder().description("Eggs").build()).block();

            System.out.println("Loaded categories: " + categoryRepository.count().block());
        }

        if (vendorRepository.count().block() == 0) {
            vendorRepository.save(Vendor.builder().fistName("Joe").lastName("Buck").build()).block();
            vendorRepository.save(Vendor.builder().fistName("Michael").lastName("Weston").build()).block();
            vendorRepository.save(Vendor.builder().fistName("Jessie").lastName("Waters").build()).block();
            vendorRepository.save(Vendor.builder().fistName("Bill").lastName("Mershi").build()).block();
            vendorRepository.save(Vendor.builder().fistName("Jimmy").lastName("Buffett").build()).block();

            System.out.println("Loaded vendors: " + vendorRepository.count().block());
        }

    }
}
