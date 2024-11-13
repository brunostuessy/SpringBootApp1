package io.brunostuessi.config;

import io.brunostuessi.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Properties;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    @Bean()
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            LOG.info("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                LOG.info(beanName);
            }

            LOG.info("Let's inspect the system properties:");

            Properties properties = System.getProperties();
            Arrays.sort(beanNames);
            for (String propertyName : properties.stringPropertyNames()) {
                LOG.info(propertyName + "=" + properties.get(propertyName));
            }

//            LOG.info("Let's run 1k sleep 10 ms tasks in virtual threads");
//
//            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
//                for (int i = 0; i < 1000; i++) {
//                    executor.submit(() -> {
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//                }
//                executor.shutdown();
//                LOG.info("" + executor.awaitTermination(5, TimeUnit.MINUTES));
//            }

            LOG.info("Let's sleep 1 hour");

            Thread.sleep(60 * 60 * 1000);

            LOG.info("Terminating");
        };
    }

}
