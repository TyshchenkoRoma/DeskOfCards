import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber" },
        features={ "src/test/resources" },
        glue = {"pack"},
        tags={ "@SmokeTest" })

public class TestRunner {

}
