package at.willhaben.tech.camel.testing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AnimalRoute extends RouteBuilder {

    public static final String CAMEL_FILE_NAME = "CamelFileName";
    private String entryPoint;
    private String firstEndpoint;
    private String secondEndpoint;

    @Autowired
    public AnimalRoute(@Value("${animalSource}") String animalSource,
                       @Value("${dogEndpoint}") String dogEndpoint,
                       @Value("${catEndpoint}") String catEndpoint) {

        this.entryPoint = animalSource;
        this.firstEndpoint = dogEndpoint;
        this.secondEndpoint = catEndpoint;
    }

    @Override
    public void configure() throws Exception {

        from(entryPoint)
                .log("got message")
                .choice()
                .when(p -> p.getIn().getHeader(CAMEL_FILE_NAME).toString().contains("dog"))
                    .log("found a dog!")
                    .to(firstEndpoint)
                .when(p -> p.getIn().getHeader("CamelFileName").toString().contains("cat"))
                .log("looks like a cat!")
                    .to(secondEndpoint);

    }
}
