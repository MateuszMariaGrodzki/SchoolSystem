package pl.com.schoolsystem.student;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Mapper(unmappedTargetPolicy = ERROR)
public interface StudentMapper {

  StudentMapper STUDENT_MAPPER = getMapper(StudentMapper.class);

  @Mapping(source = "applicationUser", target = "applicationUser")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "classs", ignore = true)
  StudentEntity toStudentEntity(ApplicationUserEntity applicationUser);

  @Mapping(source = "id", target = "id")
  StudentView toStudentView(Long id, ApplicationUserEntity entity);
}
