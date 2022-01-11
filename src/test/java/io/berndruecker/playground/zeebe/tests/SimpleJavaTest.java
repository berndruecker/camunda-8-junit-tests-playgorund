package io.berndruecker.playground.zeebe.tests;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.extensions.ZeebeProcessTest;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ZeebeProcessTest
public class SimpleJavaTest {

    private ZeebeClient client;
    private InMemoryEngine engine;

    private static boolean calledTest1 = false;
    private static int calledTestMagicNumber;

    @Test
    public void testProcessInstanceIsStarted() throws Exception {
        /*
        In very specific situations you might want to create a model on the fly just for a test case
        BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("testProcess")
                .startEvent()
                .serviceTask().zeebeJobType("serviceTaskA")
                .endEvent()
                .done();
         */
        // but more often, you simply deploy the model from classpath
        client.newDeployCommand().addResourceFromClasspath("test.bpmn").send().join();

        // when
        final Map<String, Object> variables = Collections.singletonMap("magicNumber", 42);
        ProcessInstanceEvent processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId("testProcess")
                .latestVersion()
                .variables(variables)
                .send().join();

        // then
        assertThat(processInstance).isStarted();

        assertAndExecuteJob("serviceTaskA", (jobCclient, job) -> {
            // TODO: Now we would execute our handler code that delegates to the business logic / service invocation
            // Let's do some wired static variable and non-sense sysout instead :-)
            calledTest1 = true;
            calledTestMagicNumber = (int) job.getVariablesAsMap().get("magicNumber");
            System.out.println("JIIIIHAAAAAA");
            jobCclient.newCompleteCommand(job.getKey()).send().join();
        });
        // Wait for the workflow engine to complete all asynchronously processed work, so that the assertions below work
        engine.waitForIdleState();

        assertThat(processInstance).isCompleted();
        assertTrue(calledTest1);
        assertEquals(42, calledTestMagicNumber);
    }

    private void assertAndExecuteJob(String taskType, JobHandler handler) throws Exception {
        ActivateJobsResponse job = this.client.newActivateJobsCommand()
                .jobType(taskType)
                .maxJobsToActivate(1)
                .send().join();
        handler.handle(client, job.getJobs().get(0));
    }
}
