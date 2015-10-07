package at.willhaben.tech.camel.testing;

import junit.framework.TestCase;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnimalRouteTest.class)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ComponentScan
public class AnimalRouteTest extends TestCase {

    public static final String NICE_DOG = "nice dog", NASTY_CAT="nasty cat", SUPERNASTY_CAT="super nasty cat";

    @EndpointInject(uri = "{{dogEndpoint}}")
    protected MockEndpoint dogEndpoint;

    @EndpointInject(uri = "{{catEndpoint}}")
    protected MockEndpoint catEndpoint;

    @EndpointInject(uri = "{{animalSource}}")
    protected ProducerTemplate animalSource;

    @Test
    @DirtiesContext
    public void testDog() throws Exception {

        animalSource.sendBodyAndHeader("test", AnimalRoute.CAMEL_FILE_NAME, NICE_DOG);


        dogEndpoint.message(0).predicate(m -> {
            String header = m.getIn().getHeader(AnimalRoute.CAMEL_FILE_NAME).toString();
            return NICE_DOG.equals(header);
        });
        dogEndpoint.expectedMessageCount(1);
        dogEndpoint.assertIsSatisfied();

        catEndpoint.expectedMessageCount(0);
        catEndpoint.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void testCat() throws Exception {


        animalSource.sendBodyAndHeader("test", AnimalRoute.CAMEL_FILE_NAME, NASTY_CAT);
        animalSource.sendBodyAndHeader("test", AnimalRoute.CAMEL_FILE_NAME, SUPERNASTY_CAT);


        catEndpoint.message(0).predicate(m -> {
            String header = m.getIn().getHeader(AnimalRoute.CAMEL_FILE_NAME).toString();
            return NASTY_CAT.equals(header);
        });

        catEndpoint.message(1).predicate(m -> {
            String header = m.getIn().getHeader(AnimalRoute.CAMEL_FILE_NAME).toString();
            return SUPERNASTY_CAT.equals(header);
        });

        catEndpoint.expectedMessageCount(2);

        dogEndpoint.expectedMessageCount(0);

        dogEndpoint.assertIsSatisfied();

        catEndpoint.assertIsSatisfied();



    }

}