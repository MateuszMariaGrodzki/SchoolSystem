package pl.com.schoolsystem.classs;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.teacher.TeacherEntity;

@Mapper(unmappedTargetPolicy = ERROR)
public interface ClassMapper {

  ClassMapper CLASS_MAPPER = getMapper(ClassMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "students", ignore = true)
  ClasssEntity toEntity(ClasssProfile profile, TeacherEntity supervisingTeacher);

  ClasssView toView(ClasssProfile profile);
}
