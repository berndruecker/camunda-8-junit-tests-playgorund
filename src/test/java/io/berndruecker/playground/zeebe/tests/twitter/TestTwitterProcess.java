package io.berndruecker.playground.zeebe.tests.twitter;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import io.camunda.zeebe.spring.client.config.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static io.camunda.zeebe.spring.client.config.ZeebeTestThreadSupport.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

@SpringBootTest
@ZeebeSpringTest
public class TestTwitterProcess {

    @Autowired
    private ZeebeClient zeebe;

    @Autowired
    private InMemoryEngine engine;

    @MockBean
    private TwitterService tweetPublicationService;

    @Test
    //TODO Discuss: @ZeebeTestDeployment(resources = "TwitterDemoProcess.bpmn")
    public void testHappyPath() throws Exception {
        TwitterProcessVariables variables = new TwitterProcessVariables()
            .setTweet("Hello world")
            .setApproved(true); // TODO: Add Human Task to the test

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
            .bpmnProcessId("TwitterDemoProcess").latestVersion() //
            .variables(variables).send().join();

        waitForProcessInstanceCompleted(processInstance);
        Mockito.verify(tweetPublicationService).tweet("Hello world");
    }

    @Test
    public void testDuplicate() throws Exception {
        // throw exception simulating duplicateM
        Mockito.doThrow(new DuplicateTweetException("DUPLICATE")).when(tweetPublicationService).tweet(anyString());

        TwitterProcessVariables variables = new TwitterProcessVariables()
                .setTweet("Hello world")
                .setApproved(true);

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
                .bpmnProcessId("TwitterDemoProcess").latestVersion() //
                .variables(variables).send().join();

        waitForProcessInstanceHasPassedElement(processInstance, "boundary_event_tweet_duplicated");
        // TODO: Add human task to test case
    }

    @Test
    public void testRejectionPath() throws Exception {
        TwitterProcessVariables variables = new TwitterProcessVariables();
        variables.setTweet("Hello world");
        variables.setApproved(false);

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
                .bpmnProcessId("TwitterDemoProcess").latestVersion() //
                .variables(variables).send().join();

        waitForProcessInstanceCompleted(processInstance);
        waitForProcessInstanceHasPassedElement(processInstance, "end_event_tweet_rejected");
        Mockito.verify(tweetPublicationService, never()).tweet(anyString());
    }


}
