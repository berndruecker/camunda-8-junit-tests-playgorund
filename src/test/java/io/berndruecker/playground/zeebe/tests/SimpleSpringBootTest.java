package io.berndruecker.playground.zeebe.tests;

import io.berndruecker.playground.zeebe.tests.springboot.ZeebeSpringAssertions;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.bpmnassert.assertions.BpmnAssert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@ZeebeSpringAssertions
public class SimpleSpringBootTest {

    @Autowired
    private ZeebeClient client;

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
        waitForIdleState();
        System.out.println("##############################################");
        assertThat(processInstance).isCompleted();
        assertTrue(calledTest1);
    }

    @ZeebeWorker(type="test1", autoComplete = true)
    public void handleTest1() {
        calledTest1 = true;
    }

    // TODO find a better solution for this
    public static void waitForIdleState() {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            e.printStackTrace();
            throw new IllegalStateException("Sleep was interrupted");
        }
    }
}
