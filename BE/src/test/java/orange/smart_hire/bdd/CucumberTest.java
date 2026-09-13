package orange.smart_hire.bdd;

import org.junit.platform.suite.api.*;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "orange.smart_hire.bdd")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,html:target/cucumber-reports/smart-hire-bdd-report.html"
)
public class CucumberTest {
}