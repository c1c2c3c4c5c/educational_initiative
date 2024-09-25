package com.task.DSO.DailyScheduleOrganiser.Repository;

import com.task.DSO.DailyScheduleOrganiser.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Query(value = "SELECT * FROM task WHERE date = :date ORDER BY start_time", nativeQuery = true)
    List<Task> findByDate(@Param("date") LocalDate date);
}
