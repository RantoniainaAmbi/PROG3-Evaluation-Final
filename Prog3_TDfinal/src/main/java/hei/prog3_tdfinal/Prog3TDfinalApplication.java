package hei.prog3_tdfinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Prog3TDfinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(Prog3TDfinalApplication.class, args);
    }

}
