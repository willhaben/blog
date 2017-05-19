package at.willhaben.blog.performance.testing.reactive.controller;

import at.willhaben.blog.performance.testing.reactive.model.TestData;
import at.willhaben.blog.performance.testing.reactive.repository.TestDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class TestController {

    @Autowired
    private TestDataRepository testDataRepository;

    @GetMapping("/api/")
    public Flux<TestData> getTestData() {
        return this.testDataRepository.findAll();
    }

}
