package pl.com.schoolsystem.architecture;

import static com.tngtech.archunit.library.GeneralCodingRules.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(
  packages = "pl.com.schoolsystem",
  importOptions = {
    ImportOption.DoNotIncludeTests.class,
    ImportOption.DoNotIncludeArchives.class,
    ImportOption.DoNotIncludeJars.class,
  }
)
public class GeneralCodingRulesTest {

  @ArchTest
  void shouldNotThrowGenericExceptions(JavaClasses classes) {
    NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(classes);
  }

  @ArchTest
  void shouldNotUseStandardStreams(JavaClasses classes) {
    NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(classes);
  }

  @ArchTest
  void shouldNotUseJodaTime(JavaClasses classes) {
    NO_CLASSES_SHOULD_USE_JODATIME.check(classes);
  }

  @ArchTest
  void shouldNotUseJavaUtilLogging(JavaClasses classes) {
    NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING.check(classes);
  }
}
