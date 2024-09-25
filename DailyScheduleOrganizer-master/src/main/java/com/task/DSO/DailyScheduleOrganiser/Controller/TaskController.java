package com.task.DSO.DailyScheduleOrganiser.Controller;

import com.task.DSO.DailyScheduleOrganiser.DTO.TaskRequest;
import com.task.DSO.DailyScheduleOrganiser.DTO.TaskResponse;
import com.task.DSO.DailyScheduleOrganiser.DTO.UpdatetaskRequestDTO;
import com.task.DSO.DailyScheduleOrganiser.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/taskManager/")
public class TaskController {
    private final TaskService taskService;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @PostMapping()
    public String addTask(@RequestBody TaskRequest taskRequest) {
        log.info("Received request to add task: {}", taskRequest);
        String response = taskService.addTask(taskRequest, taskRequest.getDate());
        log.info("Response from addTask: {}", response);
        return response;
    }

    @GetMapping("/{date}")
    public List<TaskResponse> findAllTaskForParticularDate(@PathVariable("date") LocalDate date) {
        log.info("Received request to find all tasks for date: {}", date);
        List<TaskResponse> tasks = taskService.findAllTaskForParticularDate(date);
        log.info("Found {} tasks for date: {}", tasks.size(), date);
        return tasks;
    }

    @PutMapping("/{date}/{id}")
    public TaskResponse updateTask(@PathVariable("date") LocalDate date, @PathVariable("id") int id,
                                   @RequestBody UpdatetaskRequestDTO updatetaskRequestDTO) {
        log.info("Received request to update task ID: {} for date: {}", id, date);
        TaskResponse updatedTask = taskService.updateTask(date, id, updatetaskRequestDTO);
        log.info("Task updated successfully: {}", updatedTask);
        return updatedTask;
    }

    @DeleteMapping("/{date}/{id}")
    public String deleteTask(@PathVariable("date") LocalDate date, @PathVariable("id") int id) {
        log.info("Received request to delete task ID: {} for date: {}", id, date);
        String response = taskService.deleteTask(date, id);
        log.info("Response from deleteTask: {}", response);
        return response;
    }
}
