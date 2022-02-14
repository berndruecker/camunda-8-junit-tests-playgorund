package io.berndruecker.playground.zeebe.tests.twitter;

import io.camunda.zeebe.spring.client.annotation.ZeebeVariablesAsType;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TwitterWorker {

    @Autowired
    private TwitterService twitterService;

    @ZeebeWorker( type = "publish-tweet", autoComplete = true)
    public void handleTweet(@ZeebeVariablesAsType TwitterProcessVariables variables) throws Exception {
        try {
            twitterService.tweet(variables.getTweet());
        } catch (DuplicateTweetException ex) {
            throw new ZeebeBpmnError("duplicateMessage", "Could not post tweet, it is a duplicate.");
        }
    }

    @ZeebeWorker( type = "send-rejection", autoComplete = true)
    public void sendRejection(@ZeebeVariablesAsType TwitterProcessVariables variables) throws Exception {
        // same thing as above, do data transformation and delegate to real business code / service
    }

}
