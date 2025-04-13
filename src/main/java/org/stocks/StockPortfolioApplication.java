package org.stocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.stocks.config.DatabaseConfig;

import java.util.Scanner;

@SpringBootApplication
public class StockPortfolioApplication {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter DB Username: ");
    String user = scanner.nextLine();

    System.out.print("Enter DB Password: ");
    String pass = scanner.nextLine();

    DatabaseConfig.DB_USERNAME = user;
    DatabaseConfig.DB_PASSWORD = pass;

    SpringApplication.run(StockPortfolioApplication.class, args);
  }
}
