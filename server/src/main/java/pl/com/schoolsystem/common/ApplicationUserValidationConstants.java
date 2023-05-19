package pl.com.schoolsystem.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApplicationUserValidationConstants {

  public final String PHONE_NUMBER_REGEX = "\\d{9}";

  public final String PHONE_NUMBER_MESSAGE = "Phone number must have exactly 9 digits";

  public final String NAME_REGEX = "[a-zA-ZąęćóńłśżźĄĘĆÓŁŃŚŻŹ\\u0020\\-]*";

  public final String NAME_MESSAGE =
      "Invalid characters. Name can have only letters, space and dash";

  public final String EMAIL_REGEX =
      "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

  public final String EMAIL_MESSAGE = "Email has bad format";

  public final String FIRST_NAME_MANDATORY_MESSAGE = "First name is mandatory";

  public final String LAST_NAME_MANDATORY_MESSAGE = "Second name is mandatory";

  public final String PHONE_NUMBER_MANDATORY_MESSAGE = "Phone number is mandatory";

  public final String EMAIL_MANDATORY_MESSAGE = "Email is mandatory";
}
