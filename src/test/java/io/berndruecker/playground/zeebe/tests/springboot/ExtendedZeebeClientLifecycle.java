package io.berndruecker.playground.zeebe.tests.springboot;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.ZeebeClientObjectFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Consumer;

public class ExtendedZeebeClientLifecycle extends ZeebeClientLifecycle {

    public ExtendedZeebeClientLifecycle(ZeebeClientObjectFactory factory, ApplicationEventPublisher publisher) {
        super(factory, publisher);
    }

    public ZeebeClientLifecycle addStartListener(Consumer<ZeebeClient> consumer) {
        super.addStartListener(consumer);
        // IN test cases the call sequence seems to be different, still need to understand why, but this seems to fix it
        if (isRunning()) {
            consumer.accept(this);
        }
        return this;
    }

}
