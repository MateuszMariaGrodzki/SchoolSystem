package pl.com.schoolsystem.school;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.com.schoolsystem.headmaster.HeadmasterEntity;

@Mapper(unmappedTargetPolicy = ERROR)
public interface SchoolMapper {

  SchoolMapper SCHOOL_MAPPER = getMapper(SchoolMapper.class);

  @Mapping(target = "headmaster", source = "headmaster")
  @Mapping(target = "id", ignore = true)
  SchoolEntity toSchoolEntity(HeadmasterEntity headmaster, SchoolCommand schoolCommand);

  @Mapping(target = "street", source = "entity.address.street")
  @Mapping(target = "city", source = "entity.address.city")
  @Mapping(target = "building", source = "entity.address.building")
  @Mapping(target = "postCode", source = "entity.address.postCode")
  SchoolView toSchoolView(SchoolEntity entity);

  @Mapping(target = "headmaster", ignore = true)
  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget SchoolEntity entity, SchoolCommand command);
}
