package org.stocks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class StockPortfolioApplication implements WebMvcConfigurer {

  @Value("${FRONTEND_URL:http://localhost:5173}")
  private String frontendUrl;

  public static void main(String[] args) {
    SpringApplication.run(StockPortfolioApplication.class, args);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(
                    "http://localhost:5173",
                    "http://localhost:3000",
                    frontendUrl
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedOrigin(frontendUrl);
    configuration.addAllowedOriginPattern("https://*.netlify.app");

    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}