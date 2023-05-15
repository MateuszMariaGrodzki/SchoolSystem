package pl.com.schoolsystem.common;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant time, String code, String message, Map<String, String> details) {}
