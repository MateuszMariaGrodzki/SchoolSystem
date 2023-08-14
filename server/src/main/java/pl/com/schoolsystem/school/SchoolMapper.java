package pl.com.schoolsystem.school;

import static org.mapstruct.ReportingPolicy.ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.com.schoolsystem.headmaster.HeadmasterEntity;

@Mapper(unmappedTargetPolicy = ERROR)
public interface SchoolMapper {

  SchoolMapper SCHOOL_MAPPER = getMapper(SchoolMapper.class);

  @Mapping(target = "headmaster", source = "headmaster")
  @Mapping(target = "id", ignore = true)
  SchoolEntity toSchoolEntity(HeadmasterEntity headmaster, SchoolCommand schoolCommand);

  SchoolView toSchoolView(SchoolEntity entity, AddressEmbeddable address);
}
