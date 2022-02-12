package io.berndruecker.playground.zeebe.tests.twitter;

import io.camunda.zeebe.spring.client.annotation.ZeebeVariablesAsType;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublishTweetWorker  {

    @Autowired
    private TweetPublicationService tweetPublicationService;

    @ZeebeWorker( type = "publish-tweet", autoComplete = true)
    public void handleTweet(@ZeebeVariablesAsType TwitterProcessVariables variables) throws Exception {
        tweetPublicationService.tweet(variables.getTweet());
    }
}
