package ro.upb.acs.fleetman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FleetmanApplication {

    static void main(String[] args) {
        SpringApplication.run(FleetmanApplication.class, args);
    }

}
