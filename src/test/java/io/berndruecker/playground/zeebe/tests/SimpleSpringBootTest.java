package io.berndruecker.playground.zeebe.tests;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.RecordStreamSourceStore;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.config.ZeebeSpringAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@ZeebeSpringAssertions
public class SimpleSpringBootTest {

    @Autowired
    private ZeebeClient client;

    @Autowired
    private InMemoryEngine engine;

    private static boolean calledTest1 = false;

    @Test
    public void testProcessInstanceIsStarted() {
        BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("test1")
                .startEvent()
                .serviceTask().zeebeJobType("test1")
                .endEvent()
                .done();

        client.newDeployCommand().addProcessModel(bpmnModel, "test1.bpmn").send().join();

        final Map<String, Object> variables = Collections.singletonMap("magicNumber", 42);

        // when
        ProcessInstanceEvent processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId("test1")
                .latestVersion()
                .variables(variables)
                .send().join();

        // then
        System.out.println("##############################################");
        assertThat(processInstance).isStarted();
        System.out.println("##############################################");
        // Currently multi-threaded as relying on the @ZeebeWorker opening up
        // its own worker via the client
        waitForCompletion(processInstance);
        System.out.println("##############################################");
        assertThat(processInstance).isCompleted();
        assertThat(processInstance).hasVariable("magicNumber");
        assertThat(processInstance).hasVariableWithValue("magicNumber", 42);
        assertTrue(calledTest1);
    }

    @ZeebeWorker(type="test1", autoComplete = true)
    public void handleTest1() {
        calledTest1 = true;
    }

    // TODO find a better solution for this
    public void waitForCompletion(ProcessInstanceEvent processInstance) {
        Awaitility.await().atMost(Duration.ofMillis(1000)).untilAsserted(() -> {
            RecordStreamSourceStore.init(engine.getRecordStream());
            assertThat(processInstance).isCompleted();
            Thread.sleep(100L);
        });
    }
}
