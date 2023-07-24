package pl.com.schoolsystem.security.user;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.admin.AdministratorCommand;
import pl.com.schoolsystem.headmaster.HeadmasterCommand;
import pl.com.schoolsystem.teacher.TeacherCommand;

@Mapper(unmappedTargetPolicy = ERROR)
public interface ApplicationUserMapper {

  ApplicationUserMapper APPLICATION_USER_MAPPER = getMapper(ApplicationUserMapper.class);

  AddApplicationUserCommand toApplicationUserCommand(
      AdministratorCommand command, String password, ApplicationRole role);

  AddApplicationUserCommand toApplicationUserCommand(
      HeadmasterCommand command, String password, ApplicationRole role);

  AddApplicationUserCommand toApplicationUserCommand(
      TeacherCommand command, String password, ApplicationRole role);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "expired", ignore = true)
  ApplicationUserEntity toApplicationUserEntity(AddApplicationUserCommand command);
}
