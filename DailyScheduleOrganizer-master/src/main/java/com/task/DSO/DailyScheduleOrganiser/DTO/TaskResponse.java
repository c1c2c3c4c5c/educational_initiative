package com.task.DSO.DailyScheduleOrganiser.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskResponse {
    private int id;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int priority;
}
