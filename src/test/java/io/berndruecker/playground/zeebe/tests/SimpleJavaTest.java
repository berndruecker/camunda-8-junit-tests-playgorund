package io.berndruecker.playground.zeebe.tests;

import io.camunda.zeebe.bpmnassert.extensions.ZeebeProcessTest;
import io.camunda.zeebe.bpmnassert.testengine.InMemoryEngine;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.bpmnassert.assertions.BpmnAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ZeebeProcessTest
public class SimpleJavaTest {

    private ZeebeClient client;
    private InMemoryEngine engine;

    private static boolean calledTest1 = false;
    private static int calledTestMagicNumber;

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

        client.newWorker().jobType("test1").handler((jobCclient, job) -> {
            calledTest1 = true;
            calledTestMagicNumber = (int) job.getVariablesAsMap().get("magicNumber");
            System.out.println("JIIIIHAAAAAA");
            jobCclient.newCompleteCommand(job.getKey()).send().join();
        }).open();

        // then
        System.out.println("##############################################");
        assertThat(processInstance).isStarted();
        System.out.println("##############################################");
        engine.waitForIdleState();
        waitForIdleState();
        System.out.println("##############################################");
        assertThat(processInstance).isCompleted();
        assertTrue(calledTest1);
        assertEquals(42, calledTestMagicNumber);
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
