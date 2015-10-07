package at.willhaben.tech.camel.testing;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


/**
 * Use this class to start the camel process. configurableApplicationContextBean.blockMainThread() will
 * make it run indefinitely.
 */

@EnableAutoConfiguration
@ComponentScan
public class Main {

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(Main.class, args);

        CamelSpringBootApplicationController configurableApplicationContextBean = configurableApplicationContext.getBean(CamelSpringBootApplicationController.class);
        configurableApplicationContextBean.blockMainThread();


    }
}
