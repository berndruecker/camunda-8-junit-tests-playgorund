package io.berndruecker.playground.zeebe.tests;

import io.camunda.zeebe.process.test.spring.ZeebeSpringTest;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.process.test.spring.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static io.camunda.zeebe.process.test.spring.ZeebeTestThreadSupport.waitForProcessInstanceHasPassedElement;
import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;

@SpringBootTest
@ZeebeSpringTest
public class SimpleSpringBootTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpringBootTest.class);

    @Autowired private ZeebeClient client;

    @Autowired private InMemoryEngine engine;

    @Test
    public void testProcessInstanceIsStarted() {
        final Map<String, Object> variables = Collections.singletonMap("a", 42);

        // when
        ProcessInstanceEvent processInstance = client.newCreateInstanceCommand().bpmnProcessId("testProcess")
                                                     .latestVersion().variables(variables).send().join();

        // then
        assertThat(processInstance).isStarted();
        LOGGER.info("Started process instance {}", processInstance.getProcessInstanceKey());
        waitForProcessInstanceCompleted(processInstance);
        assertThat(processInstance).hasVariable("b");
        assertThat(processInstance).hasVariableWithValue("b", 42); // still the problem with the value! // FIXME
    }

    @Test
    public void testProcessInstanceIsStartedAgain() {
        final Map<String, Object> variables = Collections.singletonMap("a", 42);

        // when
        ProcessInstanceEvent processInstance = client.newCreateInstanceCommand().bpmnProcessId("testProcess")
                                                     .latestVersion().variables(variables).send().join();

        assertThat(processInstance).isStarted();
        LOGGER.info("Started process instance {}", processInstance.getProcessInstanceKey());
        waitForProcessInstanceCompleted(processInstance);
        assertThat(processInstance).hasVariable("b");
        assertThat(processInstance).hasVariableWithValue("b", 42);
    }

    @Test
    public void testProcessInstanceIsStartedAgainAgain() {
        final Map<String, Object> variables = Collections.singletonMap("a", 42);

        // when
        ProcessInstanceEvent processInstance = client.newCreateInstanceCommand().bpmnProcessId("testProcess")
                                                     .latestVersion().variables(variables).send().join();

        assertThat(processInstance).isStarted();
        LOGGER.info("Started process instance {}", processInstance.getProcessInstanceKey());
        waitForProcessInstanceHasPassedElement(processInstance, "service_task_A");
        assertThat(processInstance).hasVariable("b");
        assertThat(processInstance).hasVariableWithValue("b", 42);
    }
}
