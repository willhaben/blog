package at.willhaben.blog.camel.weatherservice.infrastructure;

import javax.annotation.PostConstruct;

public interface CustomCamelSpringBootAppController {
    @PostConstruct
    void init();

    void blockMainThread();

    void stop();
}
