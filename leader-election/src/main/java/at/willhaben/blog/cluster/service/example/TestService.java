package at.willhaben.blog.cluster.service.example;

import at.willhaben.blog.cluster.service.infrastructure.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class TestService implements ClusterService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TestService.class);

    private AtomicBoolean keepRunning = new AtomicBoolean(true);

    private AtomicBoolean isMaster = new AtomicBoolean(false);

    public void goToMasterMode() {
        LOGGER.info("goToMasterMode");
        isMaster.set(true);

    }

    public void goToSlaveMode() {
        LOGGER.info("goToSlaveMode");
        isMaster.set(false);
    }

    public void shutdown() {
        keepRunning.set(false);
    }

    public void doSomeWork() throws InterruptedException {
        while (keepRunning.get()) {

            Thread.sleep(1000);

            if (isMaster.get()) {
                LOGGER.info("Doing some work ...");
            } else {
                LOGGER.info("Doing nothing ...");
            }

        }
    }
}
