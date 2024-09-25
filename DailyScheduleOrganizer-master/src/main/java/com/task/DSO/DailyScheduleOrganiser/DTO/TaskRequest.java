package com.task.DSO.DailyScheduleOrganiser.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int priority;
}
