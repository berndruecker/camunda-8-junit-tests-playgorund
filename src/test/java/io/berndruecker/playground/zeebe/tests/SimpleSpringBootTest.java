package io.berndruecker.playground.zeebe.tests;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;

@SpringBootTest
@ZeebeSpringTest
public class SimpleSpringBootTest {

    @Autowired
    private ZeebeClient client;

    @Autowired
    private ZeebeTestEngine engine;

    private static boolean calledTest1 = false;

    @Test
    public void testProcessInstanceIsStarted() {
        final Map<String, Object> variables = Collections.singletonMap("a", 42);

        // when
        ProcessInstanceEvent processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId("testProcess")
                .latestVersion()
                .variables(variables)
                .send().join();

        // then
        System.out.println("##############################################");
        assertThat(processInstance).isStarted();
        System.out.println("##############################################");
        // Currently multi-threaded as relying on the @ZeebeWorker opening up
        // its own worker via the client
        //engine.waitForIdleState();
        waitForProcessInstanceCompleted(processInstance);
        System.out.println("##############################################");
        //assertThat(processInstance).isCompleted();
        assertThat(processInstance).hasVariable("b");
        assertThat(processInstance).hasVariableWithValue("b", "42");
    }

}
