package io.berndruecker.playground.zeebe.tests.twitter;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.config.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

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
    private TweetPublicationService tweetPublicationService;

    @Test
    //@ZeebeTestDeployment(resources = "TwitterDemoProcess.bpmn")
    public void testHappyPath() {
        TwitterProcessVariables variables = new TwitterProcessVariables();
        variables.setTweet("Hello world");
        variables.setApproved(true);

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
            .bpmnProcessId("TwitterDemoProcess").latestVersion() //
            .variables(variables).send().join();

        waitForProcessInstanceCompleted(processInstance);
        Mockito.verify(tweetPublicationService).tweet("Hello world");
    }

    public void testDuplicate() {
        // throw exception simulating duplicateM
        Mockito.doThrow(new RuntimeException("DUPLICATE")).when(tweetPublicationService).tweet(anyString());

        // ...
    }

    public void testRejectionPath() {
        TwitterProcessVariables variables = new TwitterProcessVariables();
        variables.setTweet("Hello world");
        variables.setApproved(false);

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
                .bpmnProcessId("TwitterDemoProcess").latestVersion() //
                .variables(variables).send().join();

        waitForProcessInstanceCompleted(processInstance);
        Mockito.verify(tweetPublicationService, never());
    }


}
