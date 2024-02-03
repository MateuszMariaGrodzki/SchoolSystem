package pl.com.schoolsystem.classs;

import static pl.com.schoolsystem.classs.ClassMapper.CLASS_MAPPER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.teacher.TeacherNotFoundException;
import pl.com.schoolsystem.teacher.TeacherService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClasssService {

  private final TeacherService teacherService;

  private final ClasssRepository classsRepository;

  public ClasssView create(long teacherId, ClasssCommand command) {
    final var teacher =
        teacherService
            .findByIdAndLoggedUser(teacherId)
            .orElseThrow(() -> new TeacherNotFoundException(teacherId));
    final var classs = CLASS_MAPPER.toEntity(command.profile(), teacher);
    classsRepository.save(classs);
    log.info("Created new class for teacher {}", teacherId);
    return CLASS_MAPPER.toView(command.profile());
  }
}
