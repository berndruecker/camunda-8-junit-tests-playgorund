package io.berndruecker.playground.zeebe.tests;

import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Component
public class ServiceTaskAGlueCode {

    @Autowired
    private SomeRestClient restClient;

    @ZeebeWorker(type = "serviceTaskA", autoComplete = true)
    public Map<String, Object> executeGlueCode(@ZeebeVariable Integer a) {
        Object b = restClient.invoke(a);
        System.out.println(" ADAPTER INVOKED REST ");
        return Collections.singletonMap("b", b);
    }
}
