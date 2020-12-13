package rso.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
//@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@SpringBootApplication(scanBasePackages = "rso.frontend")
public class FrontendApplication
{

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

}

