package pt.challenge.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for the application's caching layer.
 * <p>
 * Uses Caffeine as the caching provider with customized TTL (Time-To-Live)
 * and size constraints for different types of data.
 * </p>
 */
@Configuration
public class CacheConfig {

  /**
   * Configures the CacheManager with specialized TTLs for User Profiles and Product Catalog.
   *
   * @return the configured CacheManager
   */
  @Bean
  @Primary
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("userProfiles", "products");
    cacheManager.registerCustomCache("userProfiles", Caffeine.newBuilder()
        .expireAfterWrite(24, TimeUnit.HOURS)
        .maximumSize(10000)
        .build());
    cacheManager.registerCustomCache("products", Caffeine.newBuilder()
        .expireAfterWrite(2, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build());
    return cacheManager;
  }
}
