package com.task.DSO.DailyScheduleOrganiser.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.internal.build.AllowNonPortable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatetaskRequestDTO {
    private Optional<String> description = Optional.empty();
    private Optional<LocalDate> date =Optional.empty();
    private Optional<LocalTime> startTime=Optional.empty();
    private Optional<LocalTime> endTime= Optional.empty();
    private Optional<Integer> priority = Optional.empty();
}
