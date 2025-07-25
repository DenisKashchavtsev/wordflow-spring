package pro.dkart.wordflow;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModulithArchitectureTests {

    @Test
    public void writeDocumentation() {
        var modules = ApplicationModules.of(WordflowApplication.class).verify();
        modules.verify();
        new Documenter(modules)
                .writeModulesAsPlantUml();
    }
}