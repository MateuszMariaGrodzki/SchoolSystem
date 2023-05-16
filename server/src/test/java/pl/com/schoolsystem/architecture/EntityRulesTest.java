package pl.com.schoolsystem.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import jakarta.persistence.Entity;

@AnalyzeClasses(
    packages = "pl.com.schoolsystem",
    importOptions = {
      ImportOption.DoNotIncludeTests.class,
      ImportOption.DoNotIncludeArchives.class,
      ImportOption.DoNotIncludeJars.class,
    })
public class EntityRulesTest {

  @ArchTest
  void shouldHaveEntitySuffix(JavaClasses javaClasses) {
    classes()
        .that()
        .areAnnotatedWith(Entity.class)
        .should()
        .haveSimpleNameEndingWith("Entity")
        .because("We want consistent names")
        .check(javaClasses);
  }

  @ArchTest
  void shouldHaveEntityAnnotation(JavaClasses javaClasses) {
    classes()
        .that()
        .haveSimpleNameEndingWith("Entity")
        .should()
        .beAnnotatedWith(Entity.class)
        .because("We want consistent names")
        .check(javaClasses);
  }
}
