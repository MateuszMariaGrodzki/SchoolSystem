package pl.com.schoolsystem.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApplicationUserValidationConstants {

  public final String PHONE_NUMBER_REGEX = "//d{9}";

  public final String PHONE_NUMBER_MESSAGE = "Phone number must have exactly 9 digits";

  public final String NAME_REGEX = "[a-zA-ZąęćóńłśżźĄĘĆÓŁŃŚŻŹ\\u0020\\-]*";

  public final String NAME_MESSAGE =
      "Invalid characters. Name can have only letters, space and dash";

  public final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$";

  public final String EMAIL_MESSAGE = "Email has bad format";

  public final String FIRST_NAME_MANDATORY_MESSAGE = "first name is mandatory";

  public final String LAST_NAME_MANDATORY_MESSAGE = "second name is mandatory";

  public final String PHONE_NUMBER_MANDATORY_MESSAGE = "phone number is mandatory";

  public final String EMAIL_MANDATORY_MESSAGE = "email is mandatory";
}
