package at.willhaben.tech.camel.testing;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@EnableAutoConfiguration
@ComponentScan
@Configuration
@PropertySource(value={"classpath:application-test.properties"})
@Profile("test")
public class TestConfiguration {
}
