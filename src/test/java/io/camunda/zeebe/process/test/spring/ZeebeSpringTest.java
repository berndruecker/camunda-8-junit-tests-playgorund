package io.camunda.zeebe.process.test.spring;

import io.camunda.zeebe.spring.client.config.ZeebeTestClientSpringConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This should go into zeebe-testing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
// this creates the engine and the client
@Import({ZeebeTestClientSpringConfiguration.class})
// this listener hooks up into test execution
@TestExecutionListeners(listeners = ZeebeTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface ZeebeSpringTest {

}
