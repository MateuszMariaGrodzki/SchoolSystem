package pl.com.schoolsystem.security.user;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(unmappedTargetPolicy = ERROR)
public interface ApplicationUserMapper {

  ApplicationUserMapper APPLICATION_USER_MAPPER = getMapper(ApplicationUserMapper.class);

  @Mapping(target = "email", expression = "java(command.email().toLowerCase())")
  AddApplicationUserCommand toApplicationUserCommand(
      UserCommand command, String password, ApplicationRole role);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "expired", ignore = true)
  ApplicationUserEntity toApplicationUserEntity(AddApplicationUserCommand command);
}
