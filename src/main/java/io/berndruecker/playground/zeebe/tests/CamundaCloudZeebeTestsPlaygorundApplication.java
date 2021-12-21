package io.berndruecker.playground.zeebe.tests;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = "classpath*:*.bpmn")
public class CamundaCloudZeebeTestsPlaygorundApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamundaCloudZeebeTestsPlaygorundApplication.class, args);
	}

}
