package highsquare.hirecoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class HiRecoderApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiRecoderApplication.class, args);
	}

	@Configuration
	@EnableJpaAuditing
	public static class JpaAuditingConfiguration {
	}
}
