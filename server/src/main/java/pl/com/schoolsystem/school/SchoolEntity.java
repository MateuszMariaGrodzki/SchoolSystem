package pl.com.schoolsystem.school;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.com.schoolsystem.headmaster.HeadmasterEntity;

@Entity
@Table(name = "school")
@Getter
@Setter
public class SchoolEntity {

  @Id
  @SequenceGenerator(name = "school_id_seq", sequenceName = "school_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "school_id_seq")
  private Long id;

  private String name;

  @Enumerated(EnumType.STRING)
  private SchoolLevel tier;

  @Embedded private AddressEmbeddable address;

  @OneToOne @MapsId private HeadmasterEntity headmaster;
}
