package pl.com.schoolsystem.teacher;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Mapper(unmappedTargetPolicy = ERROR)
public interface TeacherMapper {

  TeacherMapper TEACHER_MAPPER = getMapper(TeacherMapper.class);

  @Mapping(source = "applicationUser", target = "applicationUser")
  @Mapping(target = "id", ignore = true)
  TeacherEntity toTeacherEntity(ApplicationUserEntity applicationUser);

  @Mapping(source = "id", target = "id")
  TeacherView toTeacherView(Long id, ApplicationUserEntity entity);
}
