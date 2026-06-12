package com.fooddelivery.eurekaServer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;

@SpringBootTest(properties = {
		"eureka.client.enabled=false"
})
class EurekaServerApplicationTests {

	@Test
	void contextLoads() {
	}
}