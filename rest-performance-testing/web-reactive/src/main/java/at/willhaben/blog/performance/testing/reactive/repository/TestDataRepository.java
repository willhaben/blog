package at.willhaben.blog.performance.testing.reactive.repository;


import at.willhaben.blog.performance.testing.reactive.model.TestData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TestDataRepository extends ReactiveCrudRepository<TestData, Long> {
}
