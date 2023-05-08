package pl.com.schoolsystem.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.lang.annotation.Annotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@AnalyzeClasses(
  packages = "pl.com.schoolsystem",
  importOptions = {
    ImportOption.DoNotIncludeTests.class,
    ImportOption.DoNotIncludeArchives.class,
    ImportOption.DoNotIncludeJars.class,
  }
)
public class SpringRulesTest {

  @ArchTest
  void noClassShouldUseAutowired(JavaClasses classes) {
    noClasses()
        .should()
        .beAnnotatedWith(Autowired.class)
        .because("It is not required to add @Autowired in Spring components.")
        .check(classes);
  }

  @ArchTest
  void noFieldsShouldBeAnnotatedByAutowired(JavaClasses javaClasses) {
    NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(javaClasses);
  }

  @ArchTest
  void noPrivateMethodsShouldBeAnnotatedWithTransactional(JavaClasses javaClasses) {
    noMethods()
        .that()
        .arePrivate()
        .should()
        .beAnnotatedWith(Transactional.class)
        .because("Proxy can't call method if it's private")
        .check(javaClasses);
  }

  @ArchTest
  void methodsCalledFromSameClassShouldNotBeAnnotatedByTransactional(JavaClasses javaClasses) {
    noClasses()
        .should(callMethodsFromTheSameClass(Transactional.class))
        .because(
            "@Transactional methods should never be called from the same class because no proxy can be used then")
        .check(javaClasses);
  }

  private static <T> ArchCondition<JavaClass> callMethodsFromTheSameClass(
      Class<? extends Annotation> clazz) {
    return new ArchCondition<JavaClass>(
        String.format("call @%s Methods from the same class", clazz.getName())) {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        for (JavaMethodCall call : javaClass.getMethodCallsFromSelf()) {
          boolean originIsTarget = call.getOriginOwner().equals(call.getTargetOwner());
          if (call.getTarget().isAnnotatedWith(clazz)) {
            events.add(new SimpleConditionEvent(call, originIsTarget, call.getDescription()));
          }
        }
      }
    };
  }
}
