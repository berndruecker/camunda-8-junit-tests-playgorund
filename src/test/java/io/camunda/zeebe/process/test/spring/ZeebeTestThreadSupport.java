package io.camunda.zeebe.process.test.spring;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.RecordStreamSourceStore;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Objects;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;

/**
 * Hacky solution to work with threads.
 */
public class ZeebeTestThreadSupport {

    private final static ThreadLocal<InMemoryEngine> engines = new ThreadLocal<>();

    public static void setEngineForCurrentThread(InMemoryEngine engine) {
        engines.set(engine);
    }

    public static void cleanupEngineForCurrentThread() {
        engines.remove();
    }

    public static void waitForProcessInstanceCompleted(ProcessInstanceEvent processInstance) {
        // get it in the thread of the test
        final InMemoryEngine engine = engines.get();
        Awaitility.await().atMost(Duration.ofMillis(5000)).untilAsserted(() -> {
            // allow the worker to work
            Thread.sleep(500L);
            RecordStreamSourceStore.init(Objects.requireNonNull(engine).getRecordStream());
            // use inside the awaitility thread
            assertThat(processInstance).isCompleted();
        });
    }

    public static void waitForProcessInstanceHasPassedElement(ProcessInstanceEvent processInstance, String elementId) {
        final InMemoryEngine engine = engines.get();
        Awaitility.await().atMost(Duration.ofMillis(5000)).untilAsserted(() -> {
            Thread.sleep(500L);
            RecordStreamSourceStore.init(Objects.requireNonNull(engine).getRecordStream());
            assertThat(processInstance).hasPassedElement(elementId);
        });
    }
}
