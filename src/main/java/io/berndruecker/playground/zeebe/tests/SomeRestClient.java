package io.berndruecker.playground.zeebe.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SomeRestClient {

    private static final Logger logger = LoggerFactory.getLogger(SomeRestClient.class);

    public Object invoke(Object a) {
        logger.info(" REST ENDPOINT INVOKED ");
        return 42;
    }
}
