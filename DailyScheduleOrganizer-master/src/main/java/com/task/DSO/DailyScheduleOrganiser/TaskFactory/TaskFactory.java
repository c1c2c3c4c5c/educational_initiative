package com.task.DSO.DailyScheduleOrganiser.TaskFactory;

import com.task.DSO.DailyScheduleOrganiser.Entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class TaskFactory {

    public Task createTask(String description, LocalTime startTime, LocalTime endTime, int priority, LocalDate date) {
        return new Task(description, date, startTime,endTime,priority);
    }
}