package io.berndruecker.playground.zeebe.tests.twitter;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
public class ReviewTweetRestApi {

    @Autowired
    private ZeebeClient zeebeClient;

    @PutMapping("/orchestrate")
    public ResponseEntity<String> startTweetReviewProcess(ServerWebExchange exchange) {
        // TODO: add data to the process instance from REST request

        // start the process instance within Zeebe using a blocking call
        ProcessInstanceEvent processInstance = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("TwitterDemoProcess")
                .latestVersion()
                //.variables(variables)
                .send().join();

        // And just return something for the sake of the example
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Started process instance with key: " + processInstance.getProcessInstanceKey());
    }
}
