package at.willhaben.blog.camel.weatherservice.infrastructure;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.MainSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;


/**
 * Is used to block the main thread as long as the
 * route is running.
 *
 * Except the standard implementation this can be shutdown in a proper way
 */
@Service
public class CustomCamelSpringBootAppControllerImpl implements CustomCamelSpringBootAppController {
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    private MainSupport mainSupport;

    @Override
    @PostConstruct
    public void init()  {
        this.mainSupport = new MainSupport() {
            @Override
            protected ProducerTemplate findOrCreateCamelTemplate() {
                return producerTemplate;
            }

            @Override
            protected Map<String, CamelContext> getCamelContextMap() {
                return Collections.singletonMap("camelContext", camelContext);
            }
        };
    }

    @Override
    public void blockMainThread() {
        try {
            mainSupport.enableHangupSupport();
            mainSupport.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            mainSupport.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
