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
    final var schoolId = savedEntity.getId();
    log.info(
        "Created new school entity with id {} for headmaster {}", schoolId, headmaster.getId());
    return SCHOOL_MAPPER.toSchoolView(savedEntity, savedEntity.getAddress());
  }
}
