package pl.com.schoolsystem.mail;

import static java.lang.String.format;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

  private final JavaMailSender javaMailSender;

  public void sendNewUserEmail(ApplicationUserEntity user, String password) {
    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
      helper.setTo(user.getEmail());
      helper.setFrom("SchoolSchool@gmail.com");
      helper.setSubject("New account");
      helper.setText(
          format(
              "Hello in school system app. <br> <br> For login purpose the random password was generated for you. <br> <br> Your login credentials are as follow: <br> Username: %s <br> Password: %s <br> <br> Welcome to our system",
              user.getEmail(), password),
          true);

      javaMailSender.send(mimeMessage);
    } catch (MessagingException exception) {
      log.error("Failed to send email to user: {}", user.getEmail());
      throw new FailedEmailSendException();
    }
  }
}
