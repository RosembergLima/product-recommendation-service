package pt.challenge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA auditing.
 * <p>
 * This enables the automatic population of auditing fields such as 
 * {@code @CreatedDate} in entities.
 * </p>
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}