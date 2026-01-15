package com.wesleysilva.bappoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class
})
public class BappointBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BappointBackendApplication.class, args);
	}
}
