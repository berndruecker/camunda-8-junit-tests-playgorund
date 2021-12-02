package io.berndruecker.playground.zeebe.tests.springboot;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//@EmbeddedZeebeEngine
@Import(ZeebeTestClient.class)
@ExtendWith(ZeebeSpringAssertionsExtension.class)
public @interface ZeebeSpringAssertions {}
