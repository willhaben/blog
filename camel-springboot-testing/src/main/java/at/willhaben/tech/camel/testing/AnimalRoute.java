package at.willhaben.tech.camel.testing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AnimalRoute extends RouteBuilder {

    public static final String CAMEL_FILE_NAME = "CamelFileName";

    @Override
    public void configure() throws Exception {

        from("{{animalSource}}")
                .log("got message")
                .choice()
                .when(p -> p.getIn().getHeader(CAMEL_FILE_NAME).toString().contains("dog"))
                    .log("found a dog!")
                    .to("{{dogEndpoint}}")
                .when(p -> p.getIn().getHeader(CAMEL_FILE_NAME).toString().contains("cat"))
                    .log("looks like a cat!")
                    .to("{{catEndpoint}}");
    }
}
