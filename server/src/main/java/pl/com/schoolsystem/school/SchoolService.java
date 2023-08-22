package pl.com.schoolsystem.school;

import static pl.com.schoolsystem.school.SchoolMapper.SCHOOL_MAPPER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.headmaster.HeadmasterEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {

  private final SchoolRepository schoolRepository;

  public SchoolView create(HeadmasterEntity headmaster, SchoolCommand command) {
    final var entity = SCHOOL_MAPPER.toSchoolEntity(headmaster, command);
    final var savedEntity = schoolRepository.save(entity);
    log.info(
        "Created new school entity with id {} for headmaster {}",
        savedEntity.getId(),
        headmaster.getId());
    return SCHOOL_MAPPER.toSchoolView(savedEntity);
  }

  public SchoolView update(SchoolEntity entity, SchoolCommand command) {
    SCHOOL_MAPPER.update(entity, command);
    return SCHOOL_MAPPER.toSchoolView(entity);
  }
}
