package at.willhaben.blog.performance.testing.http.repository;


import at.willhaben.blog.performance.testing.http.model.TestData;
import org.springframework.data.repository.CrudRepository;

public interface TestDataRepository extends CrudRepository<TestData, Long> {
}
