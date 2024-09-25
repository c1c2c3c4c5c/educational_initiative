package com.task.DSO.DailyScheduleOrganiser;

import com.task.DSO.DailyScheduleOrganiser.DTO.TaskRequest;
import com.task.DSO.DailyScheduleOrganiser.DTO.TaskResponse;
import com.task.DSO.DailyScheduleOrganiser.DTO.UpdatetaskRequestDTO;
import com.task.DSO.DailyScheduleOrganiser.Exception.InvalidDateTimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class DailyScheduleOrganiserApplication {
	private static final Logger log = LoggerFactory.getLogger(DailyScheduleOrganiserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DailyScheduleOrganiserApplication.class, args);
		log.info("Application Started");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) {
		return args -> {
			Scanner scanner = new Scanner(System.in);
			boolean exit = false;

			while (!exit) {
				System.out.println("\n=== Task Manager ===");
				System.out.println("1. Add a task");
				System.out.println("2. View tasks for a particular date");
				System.out.println("3. Update a task");
				System.out.println("4. Delete a task");
				System.out.println("5. Exit");
				System.out.print("Choose an option: ");

				int choice = scanner.nextInt();
				scanner.nextLine();  // Consume newline

				switch (choice) {
					case 1:
						addTask(restTemplate, scanner);
						break;
					case 2:
						viewTasksForDate(restTemplate, scanner);
						break;
					case 3:
						updateTask(restTemplate, scanner);
						break;
					case 4:
						deleteTask(restTemplate, scanner);
						break;
					case 5:
						exit = true;
						System.out.println("Exiting...");
						System.exit(0);
						break;
					default:
						System.out.println("Invalid option. Please try again.");
				}
			}
		};
	}

	private void addTask(RestTemplate restTemplate, Scanner scanner) {
		System.out.println("\n--- Add a Task ---");
		System.out.print("Enter task description: ");
		String description = scanner.nextLine();
		System.out.print("Enter start time (HH:mm): ");
		String startTimeStr = scanner.nextLine();
		System.out.print("Enter end time (HH:mm): ");
		String endTimeStr = scanner.nextLine();
		System.out.print("Enter task priority: ");
		int priority =getTaskPriority(scanner);
		scanner.nextLine();
		System.out.print("Enter task date (yyyy-mm-dd): ");
		String dateStr = scanner.nextLine();

		LocalTime startTime;
		LocalTime endTime;
		LocalDate date;

		try {
			startTime = LocalTime.parse(startTimeStr);
			endTime = LocalTime.parse(endTimeStr);
			date = LocalDate.parse(dateStr);
		} catch (Exception e) {
			log.error("Invalid date or time format", e);
			throw new InvalidDateTimeException("Invalid date or time format. Please use the correct format.");
		}

		TaskRequest taskRequest = TaskRequest.builder()
				.description(description)
				.startTime(startTime)
				.endTime(endTime)
				.priority(priority)
				.date(date)
				.build();

		String result = restTemplate.postForObject("http://localhost:8080/api/taskManager/", taskRequest, String.class);
		log.info("Task added: {}", taskRequest);
		System.out.println(result);
	}

	private void viewTasksForDate(RestTemplate restTemplate, Scanner scanner) {
		System.out.println("\n--- View Tasks for a Date ---");
		System.out.print("Enter the date (yyyy-mm-dd): ");
		String dateStr = scanner.nextLine();
		LocalDate date;

		try {
			date = LocalDate.parse(dateStr);
		} catch (Exception e) {
			log.error("Invalid date format", e);
			throw new InvalidDateTimeException("Invalid date format. Please use yyyy-mm-dd.");
		}

		findTaskByDate(restTemplate, date);
	}

	private void updateTask(RestTemplate restTemplate, Scanner scanner) {
		System.out.println("\n--- Update a Task ---");

		// Get the date and ID of the task to update
		System.out.print("Enter task date (yyyy-mm-dd): ");
		String dateStr = scanner.nextLine();
		LocalDate date;

		try {
			date = LocalDate.parse(dateStr);
		} catch (Exception e) {
			log.error("Invalid date format", e);
			throw new InvalidDateTimeException("Invalid date format. Please use yyyy-mm-dd.");
		}

		findTaskByDate(restTemplate, date);
		System.out.print("Enter task ID from above tasks: ");
		int taskId = scanner.nextInt();
		scanner.nextLine();
		// Prompt user for the fields they want to update
		System.out.print("Enter new description (press Enter if not requires a change): ");
		String description = scanner.nextLine();
		Optional<String> descriptionOpt = description.isEmpty() ? Optional.empty() : Optional.of(description);

		System.out.print("Enter new start time (HH:mm, or press Enter if not requires a change): ");
		String startTimeStr = scanner.nextLine();
		Optional<LocalTime> startTimeOpt = startTimeStr.isEmpty() ? Optional.empty() : Optional.of(LocalTime.parse(startTimeStr));

		System.out.print("Enter new end time (HH:mm, or press Enter if not requires a change): ");
		String endTimeStr = scanner.nextLine();
		Optional<LocalTime> endTimeOpt = endTimeStr.isEmpty() ? Optional.empty() : Optional.of(LocalTime.parse(endTimeStr));

		System.out.print("Enter new priority (or press Enter if not requires a change): ");
		String priorityStr = scanner.nextLine();
		Optional<Integer> priorityOpt = priorityStr.isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(priorityStr));

		System.out.print("Enter new task date (yyyy-mm-dd, press Enter if not requires a change): ");
		String newDateStr = scanner.nextLine();
		Optional<LocalDate> newDateOpt = newDateStr.isEmpty() ? Optional.empty() : Optional.of(LocalDate.parse(newDateStr));

		// Build the UpdateTaskRequestDTO object
		UpdatetaskRequestDTO updateTaskRequest = UpdatetaskRequestDTO.builder()
				.description(descriptionOpt)
				.startTime(startTimeOpt)
				.endTime(endTimeOpt)
				.priority(priorityOpt)
				.date(newDateOpt)
				.build();

		String url = String.format("http://localhost:8080/api/taskManager/%s/%d", date, taskId);
		TaskResponse taskResponse = restTemplate.
				exchange(url, HttpMethod.PUT, new HttpEntity<>(updateTaskRequest), TaskResponse.class).
				getBody();
		if (taskResponse != null) {
			log.info("Task updated successfully: {}", taskResponse);
			System.out.println("Task updated successfully:");
			printTaskResponse(taskResponse);
		} else {
			log.error("Failed to update task.");
			System.out.println("Failed to update task.");
		}
	}

	private void printTaskResponse(TaskResponse task) {
		System.out.println("Task id : " + task.getId());
		System.out.println("Task Description : " + task.getDescription());
		System.out.println("Start Time       : " + task.getStartTime());
		System.out.println("End Time         : " + task.getEndTime());
		System.out.println("Priority         : " + task.getPriority());
		System.out.println("Date             : " + task.getDate());
		System.out.println("-----------------------------------");
	}

	private void deleteTask(RestTemplate restTemplate, Scanner scanner) {
		System.out.print("Enter task's date to be deleted (yyyy-mm-dd): ");
		String dateStr = scanner.nextLine();
		LocalDate date;

		try {
			date = LocalDate.parse(dateStr);
		} catch (Exception e) {
			log.error("Invalid date format", e);
			throw new InvalidDateTimeException("Invalid date format. Please use yyyy-mm-dd.");
		}

		findTaskByDate(restTemplate, date);
		System.out.print("Enter task ID from above tasks: ");
		int taskId = scanner.nextInt();
		scanner.nextLine();

		String result = restTemplate.exchange("http://localhost:8080/api/taskManager/" + date + "/" + taskId, HttpMethod.DELETE, null, String.class).getBody();
		log.info("Task deleted: date={}, taskId={}", date, taskId);
		System.out.println(result);
	}

	private void findTaskByDate(RestTemplate restTemplate, LocalDate date) {
		ResponseEntity<List<TaskResponse>> response = restTemplate.exchange(
				"http://localhost:8080/api/taskManager/{date}",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<TaskResponse>>() {},
				date
		);

		List<TaskResponse> tasks = response.getBody();
		if (tasks != null && !tasks.isEmpty()) {
			System.out.println("\nTasks for " + date + ":");
			for (TaskResponse task : tasks) {
				printTaskResponse(task);
			}
		} else {
			System.out.println("No tasks found for " + date);
		}
	}
	private static int getTaskPriority(Scanner scanner) {
		int priority;
		System.out.print("Enter task priority (1-5): ");
		priority = scanner.nextInt();
		if (priority < 1 || priority > 5) {
			System.out.println("Invalid priority! Please enter a priority between 1 and 5.");
			return getTaskPriority(scanner);
		}
		return priority;
	}
}
