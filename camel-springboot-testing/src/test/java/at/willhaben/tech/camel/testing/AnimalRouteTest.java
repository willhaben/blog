package at.willhaben.tech.camel.testing;

import junit.framework.TestCase;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TestConfiguration.class)
@ActiveProfiles(profiles = "test")
public class AnimalRouteTest extends TestCase {

    public static final String NICE_DOG = "nice dog", NASTY_CAT="nasty cat", SUPERNASTY_CAT="super nasty cat";

    @EndpointInject(uri = "mock:dogEndpoint")
    protected MockEndpoint dogEndpoint;

    @EndpointInject(uri = "mock:catEndpoint")
    protected MockEndpoint catEndpoint;

    @EndpointInject(uri = "direct:animalSource")
    protected ProducerTemplate animalSource;

    @Test
    @DirtiesContext
    public void testDog() throws Exception {

        animalSource.sendBodyAndHeader("test",AnimalRoute.CAMEL_FILE_NAME,NICE_DOG);

        dogEndpoint.expectedMessageCount(1);

        dogEndpoint.message(0).predicate(m -> {
            String header = m.getIn().getHeader(AnimalRoute.CAMEL_FILE_NAME).toString();
            return NICE_DOG.equals(header);
        });

        dogEndpoint.assertIsSatisfied();

        catEndpoint.expectedMessageCount(0);

        catEndpoint.assertIsSatisfied();



    }

    @Test
    @DirtiesContext
    public void testCat() throws Exception {


        animalSource.sendBodyAndHeader("test", AnimalRoute.CAMEL_FILE_NAME, NASTY_CAT);
        animalSource.sendBodyAndHeader("test", AnimalRoute.CAMEL_FILE_NAME, SUPERNASTY_CAT);

        dogEndpoint.expectedMessageCount(0);

        catEndpoint.message(0).predicate(m -> {
            String header = m.getIn().getHeader(AnimalRoute.CAMEL_FILE_NAME).toString();
            return NASTY_CAT.equals(header);
        });

        catEndpoint.message(1).predicate(m -> {
            String header = m.getIn().getHeader(AnimalRoute.CAMEL_FILE_NAME).toString();
            return SUPERNASTY_CAT.equals(header);
        });


        dogEndpoint.expectedMessageCount(0);

        dogEndpoint.assertIsSatisfied();

        catEndpoint.assertIsSatisfied();



    }

}