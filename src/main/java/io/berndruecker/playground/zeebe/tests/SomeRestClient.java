package io.berndruecker.playground.zeebe.tests;

import org.springframework.stereotype.Component;

@Component
public class SomeRestClient {

    public Object invoke(Object a) {
        System.out.println(" REST ENDPOINT INVOKED ");
        return "42";
    }

}
