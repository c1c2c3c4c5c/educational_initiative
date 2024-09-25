package com.task.DSO.DailyScheduleOrganiser.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull
    private String description;
    @NonNull
    private LocalDate date;
    @NonNull
    private LocalTime startTime;

    @NonNull
    private LocalTime endTime;

    private int priority;

    public Task(String description, LocalDate date, LocalTime startTime, LocalTime endTime, int priority) {
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
    }
}