package at.willhaben.blog.cluster.service;

import at.willhaben.blog.cluster.service.example.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws InterruptedException {
        LOGGER.info("Starting ...");
        ApplicationContext applicationContext = new SpringApplication(Main.class).run(args);

        TestService service = applicationContext.getBean(TestService.class);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                service.shutdown();
            }
        });

        service.doSomeWork();

        LOGGER.info("Finished ...");
    }
}
