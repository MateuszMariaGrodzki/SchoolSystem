package pl.com.schoolsystem.headmaster;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Mapper(unmappedTargetPolicy = ERROR)
public interface HeadmasterMapper {

  HeadmasterMapper HEADMASTER_MAPPER = getMapper(HeadmasterMapper.class);

  @Mapping(target = "applicationUser", source = "applicationUser")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "school", ignore = true)
  HeadmasterEntity toHeadmasterEntity(ApplicationUserEntity applicationUser);

  @Mapping(source = "id", target = "id")
  HeadmasterView toHeadmasterView(Long id, ApplicationUserEntity entity);
}
