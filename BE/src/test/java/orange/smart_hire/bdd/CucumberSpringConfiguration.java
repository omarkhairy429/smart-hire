package orange.smart_hire.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = orange.smart_hire.SmartHireApplication.class)
public class CucumberSpringConfiguration {
}