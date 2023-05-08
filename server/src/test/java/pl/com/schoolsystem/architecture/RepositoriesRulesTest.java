package pl.com.schoolsystem.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.stereotype.Repository;

@AnalyzeClasses(
  packages = "pl.com.schoolsystem",
  importOptions = {
    ImportOption.DoNotIncludeTests.class,
    ImportOption.DoNotIncludeArchives.class,
    ImportOption.DoNotIncludeJars.class,
  }
)
class RepositoriesRulesTest {

  @ArchTest
  void shouldHaveRepositorySuffix(JavaClasses javaClasses) {
    classes()
        .that()
        .areAssignableTo(org.springframework.data.repository.Repository.class)
        .should()
        .haveSimpleNameEndingWith("Repository")
        .because("We want consistent names.")
        .check(javaClasses);
  }

  @ArchTest
  void shouldNotBePublic(JavaClasses javaClasses) {
    classes()
        .that()
        .haveSimpleNameEndingWith("Repository")
        .should()
        .notBePublic()
        .because("We don't want repositories to be accessed from out of their domain package.")
        .check(javaClasses);
  }

  @ArchTest
  void shouldNotBeAnnotatedWithRepository(JavaClasses javaClasses) {
    classes()
        .that()
        .haveSimpleNameEndingWith("Repository")
        .and()
        .areInterfaces()
        .should()
        .notBeAnnotatedWith(Repository.class)
        .because("We extend spring Jpa Repositories which are anyway auto detected.")
        .check(javaClasses);
  }
}
