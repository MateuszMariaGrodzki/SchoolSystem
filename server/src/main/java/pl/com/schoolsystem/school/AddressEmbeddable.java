package pl.com.schoolsystem.school;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class AddressEmbeddable {

  private String city;

  private String street;

  private String postCode;

  private String building;
}
