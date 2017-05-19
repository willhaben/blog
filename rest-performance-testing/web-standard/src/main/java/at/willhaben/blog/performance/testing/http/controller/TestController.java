package at.willhaben.blog.performance.testing.http.controller;


import at.willhaben.blog.performance.testing.http.model.TestData;
import at.willhaben.blog.performance.testing.http.repository.TestDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestDataRepository testDataRepository;

    @GetMapping("/api/")
    public Iterable<TestData> getTestData() {
        return this.testDataRepository.findAll();
    }
}
