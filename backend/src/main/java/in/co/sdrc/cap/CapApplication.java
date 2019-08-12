package in.co.sdrc.cap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = { "org.sdrc.usermgmt.core", "in.co.sdrc.cap" })
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
		MongoDataAutoConfiguration.class })
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan(basePackages = "in.co.sdrc.cap.domain")
@EnableJpaRepositories(basePackages = { "in.co.sdrc.cap.repository" })
@EnableCaching
@Slf4j
public class CapApplication extends SpringBootServletInitializer{

	private final Path pdfOutputPath = Paths.get("/cap");
	private final Path uploadLocation = Paths.get("/cap-data");
	private final Path jsonDataLocation = Paths.get("/cap-json-data");

	public static void main(String[] args) {
		SpringApplication.run(CapApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CapApplication.class);
	}

	@PostConstruct
	public void init() {
		try {
			if (!Files.isDirectory(pdfOutputPath)) {
				Files.createDirectory(pdfOutputPath);
			}
			
			if (!Files.isDirectory(uploadLocation)) {
				Files.createDirectory(uploadLocation);
			}
			
			if (!Files.isDirectory(jsonDataLocation)) {
				Files.createDirectory(jsonDataLocation);
			}

		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
	}

}
