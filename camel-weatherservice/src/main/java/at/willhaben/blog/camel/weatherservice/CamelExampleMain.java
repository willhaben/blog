package at.willhaben.blog.camel.weatherservice;

import at.willhaben.blog.camel.weatherservice.dto.WeatherInformation;
import at.willhaben.blog.camel.weatherservice.infrastructure.CustomCamelSpringBootAppController;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.boot.FatJarRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CamelExampleMain extends FatJarRouter {

    private static Logger LOGGER = LoggerFactory.getLogger(CamelExampleMain.class);

    public static void main(String... args) {
        LOGGER.info("Starting ...");
        ApplicationContext applicationContext = new SpringApplication(CamelExampleMain.class).run(args);
        CustomCamelSpringBootAppController applicationController = applicationContext.getBean(CustomCamelSpringBootAppController.class);
        applicationController.blockMainThread();

    }

    @Override
    public void configure() {
        from("timer://trigger").
                // We are using camel http4 component
                to("http4://api.openweathermap.org/data/2.5/weather?q=London,uk").
                // We need to convert the data stream to a string for the JSON marshaler
                convertBodyTo(String.class).
                unmarshal().json(JsonLibrary.Jackson, WeatherInformation.class).
                process(exchange -> {

                    // Due to the unmarshal command a WeatherInformation is now set on the body
                    WeatherInformation weatherInformation = exchange.getIn().getBody(WeatherInformation.class);

                    // We just need the name of the city and the wind speed
                    exchange.getOut().setBody(weatherInformation.getName() + ", " + weatherInformation.getWind().getSpeed()  + System.lineSeparator());
                }).
                to("file://?fileName=test.csv&fileExist=Append").
                // Just for Logging
                to("log:out", "mock:test");
    }

}