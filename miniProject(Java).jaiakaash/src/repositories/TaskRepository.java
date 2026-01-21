package repositories;

import models.Task;

import java.util.*;
import java.util.stream.Collectors;

public class TaskRepository {

    // Stores ALL versions of ALL tasks
    private final List<Task> tasks = new ArrayList<>();

    // Save a task version
    public Task save(Task task) {
        tasks.add(task);
        return task;
    }

    // Find latest version of a task by ID
    public Optional<Task> findLatestById(UUID taskId) {
        return tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .max(Comparator.comparingInt(Task::getVersion));
    }

    // Find all versions of a task (history)
    public List<Task> findAllVersions(UUID taskId) {
        return tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .sorted(Comparator.comparingInt(Task::getVersion))
                .collect(Collectors.toList());
    }

    // Get all tasks (all versions)
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }
}
