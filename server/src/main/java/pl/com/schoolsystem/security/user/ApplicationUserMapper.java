package pl.com.schoolsystem.security.user;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.admin.AddAdministratorCommand;
import pl.com.schoolsystem.admin.AddAdministratorView;

@Mapper(unmappedTargetPolicy = ERROR)
public interface ApplicationUserMapper {

  ApplicationUserMapper APPLICATION_USER_MAPPER = getMapper(ApplicationUserMapper.class);

  AddApplicationUserCommand toApplicationUserCommand(
      AddAdministratorCommand command, String password, ApplicationRole role);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  ApplicationUserEntity toApplicationUserEntity(AddApplicationUserCommand command);

  AddAdministratorView toAdministratorView(ApplicationUserEntity entity);
}
