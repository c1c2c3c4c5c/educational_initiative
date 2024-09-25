package com.task.DSO.DailyScheduleOrganiser.Service;

import com.task.DSO.DailyScheduleOrganiser.DTO.TaskRequest;
import com.task.DSO.DailyScheduleOrganiser.DTO.TaskResponse;
import com.task.DSO.DailyScheduleOrganiser.DTO.UpdatetaskRequestDTO;
import com.task.DSO.DailyScheduleOrganiser.Entity.Task;
import com.task.DSO.DailyScheduleOrganiser.Exception.TaskNotFoundException;
import com.task.DSO.DailyScheduleOrganiser.Repository.TaskRepository;
import com.task.DSO.DailyScheduleOrganiser.TaskFactory.TaskFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskFactory taskFactory;
    private final TaskRepository taskRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Transactional
    public String addTask(TaskRequest taskRequest, LocalDate date) {
        log.info("Request to add task: {}", taskRequest);

        Task task = taskFactory.createTask(
                taskRequest.getDescription(),
                taskRequest.getStartTime(),
                taskRequest.getEndTime(),
                taskRequest.getPriority(),
                taskRequest.getDate()
        );

        List<Task> inDateTaskList = taskRepository.findByDate(date);

        if (inDateTaskList.isEmpty()) {
            taskRepository.save(task);
            log.info("Task saved successfully: {}", task);
            return "Task Saved Successfully";
        } else {
            for (Task inDate : inDateTaskList) {
                // Check if the time slot overlaps
                boolean isOverlapping = !(task.getEndTime().isBefore(inDate.getStartTime()) || task.getStartTime().isAfter(inDate.getEndTime()));

                if (isOverlapping) {
                    if (inDate.getPriority() >= task.getPriority()) {
                        log.warn("Cannot add task. A task with equal or higher priority already exists: {}", inDate);
                        return "Cannot add this task as another task already scheduled with equal or higher priority.";
                    } else {
                        log.info("Replacing task: {} with high priority task: {}", inDate, task);
                        taskRepository.delete(inDate);
                        taskRepository.save(task);
                        return "As this is a high priority task, it overrides the old task.";
                    }
                }
            }

            // If no overlapping tasks are found, save the new task
            taskRepository.save(task);
            log.info("Task saved successfully: {}", task);
            return "Task Saved Successfully";
        }
    }

    public List<TaskResponse> findAllTaskForParticularDate(LocalDate date) {
        log.info("Fetching all tasks for date: {}", date);
        List<Task> taskList = taskRepository.findByDate(date);
        return taskList.stream().map(this::mapToTaskResponse).toList();
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .description(task.getDescription())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .date(task.getDate())
                .priority(task.getPriority())
                .build();
    }

    public TaskResponse updateTask(LocalDate date, int id, UpdatetaskRequestDTO updatetaskRequestDTO) {
        log.info("Updating task with ID: {} for date: {}", id, date);
        List<Task> taskDatedForProvidedDate = taskRepository.findByDate(date);

        if (taskDatedForProvidedDate.isEmpty()) {
            throw new TaskNotFoundException("No tasks found for the provided date: " + date);
        }

        // Find the task by ID
        Task taskToBeUpdated = taskDatedForProvidedDate.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("Task not found for the given ID: " + id));

        // Update only the fields that are present in the DTO
        updatetaskRequestDTO.getDate().ifPresent(taskToBeUpdated::setDate);
        updatetaskRequestDTO.getDescription().ifPresent(taskToBeUpdated::setDescription);
        updatetaskRequestDTO.getPriority().ifPresent(taskToBeUpdated::setPriority);
        updatetaskRequestDTO.getStartTime().ifPresent(taskToBeUpdated::setStartTime);
        updatetaskRequestDTO.getEndTime().ifPresent(taskToBeUpdated::setEndTime);

        // Save the updated task in the repository
        taskRepository.save(taskToBeUpdated);
        log.info("Task updated successfully: {}", taskToBeUpdated);

        // Return the updated task as a response
        return mapToTaskResponse(taskToBeUpdated);
    }

    public String deleteTask(LocalDate date, int id) {
        log.info("Deleting task with ID: {} for date: {}", id, date);
        List<Task> taskListWithGivenDate = taskRepository.findByDate(date);
        if (taskListWithGivenDate.isEmpty()) {
            throw new TaskNotFoundException("No tasks found for the provided date: " + date);
        } else {
            Task taskToBeDeleted = taskListWithGivenDate.stream()
                    .filter(task -> task.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new TaskNotFoundException("Task not found for the given ID: " + id));
            taskRepository.delete(taskToBeDeleted);
            log.info("Task deleted successfully: {}", taskToBeDeleted);
        }
        return "Task Deleted Successfully";
    }
}