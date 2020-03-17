package example.scalecube.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExampleRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleRunner.class);

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(10);

    public static void main(String... args) throws Exception {
        LOG.info("Starting the Example...");

        Service bob = Service.builder()
                .friendlyName("Bob")
                .port(7001)
                .syncGroup("example-group")
                .build();

        Service carol = Service.builder()
                .seedAddress("localhost", 7001)
                .friendlyName("Carol")
                .syncGroup("example-group")
                .build();

        Service sally = Service.builder()
                .seedAddress("localhost", 7001)
                .friendlyName("Sally")
                .syncGroup("example-group")
                .build();

        EXECUTOR.schedule(bob, 0, TimeUnit.SECONDS);
        EXECUTOR.schedule(carol, 10, TimeUnit.SECONDS);
        EXECUTOR.schedule(sally, 20, TimeUnit.SECONDS);

        Thread.currentThread().join();
    }
}
