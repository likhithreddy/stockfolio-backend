package org.stocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootApplication
public class StockPortfolioApplication {

  public static void main(String[] args) {
    SpringApplication.run(StockPortfolioApplication.class, args);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow localhost for development
    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.addAllowedOrigin("http://localhost:3000");

    // Allow your Netlify domain (replace with actual URL when you deploy)
    configuration.addAllowedOriginPattern("https://stock-folio.netlify.app/");

    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}