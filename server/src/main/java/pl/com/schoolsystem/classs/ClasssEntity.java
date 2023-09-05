package pl.com.schoolsystem.classs;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import pl.com.schoolsystem.student.StudentEntity;
import pl.com.schoolsystem.teacher.TeacherEntity;

@Entity
@Getter
@Setter
@Table(name = "classs")
public class ClasssEntity {

  @Id
  @SequenceGenerator(name = "classs_id_seq", sequenceName = "classs_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classs_id_seq")
  private Long id;

  @Enumerated(EnumType.STRING)
  private ClasssProfile profile;

  @OneToOne
  @JoinColumn(name = "teacher_id")
  private TeacherEntity supervisingTeacher;

  @OneToMany(mappedBy = "classs")
  private List<StudentEntity> students = new ArrayList<>();
}
