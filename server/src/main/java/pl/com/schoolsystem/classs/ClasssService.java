package pl.com.schoolsystem.classs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.teacher.TeacherService;

@Service
@RequiredArgsConstructor
public class ClasssService {

  private final TeacherService teacherService;

  private final ClasssRepository classsRepository;

  public ClasssView create(long teacherId, ClasssCommand command) {
    final var teacher = teacherService.findById(teacherId);
    ClasssEntity classs = new ClasssEntity();
    classs.setProfile(command.profile());
    classs.setSupervisingTeacher(teacher);
    classsRepository.save(classs);
    return new ClasssView(command.profile());
  }
}
