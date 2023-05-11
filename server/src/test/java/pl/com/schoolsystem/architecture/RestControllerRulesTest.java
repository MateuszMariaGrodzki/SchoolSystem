package pl.com.schoolsystem.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
    packages = "pl.com.schoolsystem",
    importOptions = {
      ImportOption.DoNotIncludeTests.class,
      ImportOption.DoNotIncludeArchives.class,
      ImportOption.DoNotIncludeJars.class,
    })
public class RestControllerRulesTest {

  @ArchTest
  void shouldHaveControllerSuffix(JavaClasses javaClasses) {
    classes()
        .that()
        .areAnnotatedWith(RestController.class)
        .should()
        .haveSimpleNameEndingWith("Controller")
        .because("We want consistent names")
        .check(javaClasses);
  }

  @ArchTest
  void shouldBeAnnotatedWithRestController(JavaClasses javaClasses) {
    classes()
        .that()
        .haveSimpleNameEndingWith("Controller")
        .should()
        .beAnnotatedWith(RestController.class)
        .because("We want consistent names")
        .check(javaClasses);
  }
  // TODO [MG] after add security dependency add test for @PreAuthorize annotation on every
  // controller.
}
