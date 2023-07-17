package pl.com.schoolsystem.admin;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Mapper(unmappedTargetPolicy = ERROR)
public interface AdministratorMapper {
  AdministratorMapper ADMINISTRATOR_MAPPER = getMapper(AdministratorMapper.class);

  @Mapping(source = "applicationUser", target = "applicationUser")
  @Mapping(target = "id", ignore = true)
  AdministratorEntity toAdministratorEntity(ApplicationUserEntity applicationUser);

  @Mapping(source = "id", target = "id")
  AdministratorView toAdministratorView(Long id, ApplicationUserEntity entity);
}
