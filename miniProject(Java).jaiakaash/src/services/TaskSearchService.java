package services;

import enums.Priority;
import enums.Status;
import models.Task;
import models.User;
import repositories.TaskRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskSearchService {

    private final TaskRepository taskRepository;

    public TaskSearchService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ---------------- Internal Helper ----------------
    // Always work on latest task versions only
    private List<Task> latestTasks() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Task::getId,
                        Collectors.maxBy(Comparator.comparingInt(Task::getVersion))
                ))
                .values().stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    // ---------------- 3.1 Filter by Status ----------------
    public List<Task> filterByStatus(Set<Status> statuses) {
        return latestTasks().stream()
                .filter(t -> statuses.contains(t.getStatus()))
                .collect(Collectors.toList());
    }

    // ---------------- 3.2 Filter by Priority ----------------
    public List<Task> filterByPriority(Set<Priority> priorities) {
        return latestTasks().stream()
                .filter(t -> priorities.contains(t.getPriority()))
                .collect(Collectors.toList());
    }

    // ---------------- 3.3 Filter by Assignee ----------------
    public Map<User, List<Task>> filterByAssignee() {
        return latestTasks().stream()
                .filter(t -> t.getAssignedTo().isPresent())
                .collect(Collectors.groupingBy(t -> t.getAssignedTo().get()));
    }

    // ---------------- 3.4 Filter by Creator ----------------
    public List<Task> filterByCreator(User creator) {
        return latestTasks().stream()
                .filter(t -> t.getCreatedBy().getId().equals(creator.getId()))
                .collect(Collectors.toList());
    }

    // ---------------- 3.5 Filter Overdue Tasks ----------------
    public Map<Task, Long> findOverdueTasks() {

        Instant now = Instant.now();

        return latestTasks().stream()
                .filter(t -> t.getDueDate().isPresent())
                .filter(t -> t.getDueDate().get().isBefore(now))
                .filter(t -> t.getStatus() != Status.COMPLETED
                        && t.getStatus() != Status.CANCELLED)
                .collect(Collectors.toMap(
                        t -> t,
                        t -> ChronoUnit.DAYS.between(
                                t.getDueDate().get(), now)
                ));
    }

    // ---------------- 3.6 Filter by Date Range ----------------
    public List<Task> createdBetween(Instant from, Instant to) {
        return latestTasks().stream()
                .filter(t -> !t.getCreatedAt().isBefore(from)
                        && !t.getCreatedAt().isAfter(to))
                .collect(Collectors.toList());
    }

    public List<Task> completedBetween(Instant from, Instant to) {
        return latestTasks().stream()
                .filter(t -> t.getStatus() == Status.COMPLETED)
                .filter(t -> !t.getUpdatedAt().isBefore(from)
                        && !t.getUpdatedAt().isAfter(to))
                .collect(Collectors.toList());
    }

    public List<Task> modifiedBetween(Instant from, Instant to) {
        return latestTasks().stream()
                .filter(t -> !t.getUpdatedAt().isBefore(from)
                        && !t.getUpdatedAt().isAfter(to))
                .collect(Collectors.toList());
    }

    // ---------------- 3.7 Filter by Tags ----------------
    public List<Task> filterByTags(Set<String> tags) {
        return latestTasks().stream()
                .filter(t -> t.getTags().containsAll(tags))
                .collect(Collectors.toList());
    }

    // ---------------- 3.8 Combined Filters ----------------
    public List<Task> combinedFilter(
            Set<Status> statuses,
            Set<Priority> priorities,
            User assignee,
            boolean overdueOnly,
            Set<String> tags
    ) {

        Predicate<Task> predicate = t -> true;

        if (statuses != null && !statuses.isEmpty()) {
            predicate = predicate.and(t -> statuses.contains(t.getStatus()));
        }

        if (priorities != null && !priorities.isEmpty()) {
            predicate = predicate.and(t -> priorities.contains(t.getPriority()));
        }

        if (assignee != null) {
            predicate = predicate.and(t ->
                    t.getAssignedTo().isPresent()
                            && t.getAssignedTo().get().getId().equals(assignee.getId()));
        }

        if (overdueOnly) {
            Instant now = Instant.now();
            predicate = predicate.and(t ->
                    t.getDueDate().isPresent()
                            && t.getDueDate().get().isBefore(now)
                            && t.getStatus() != Status.COMPLETED
                            && t.getStatus() != Status.CANCELLED);
        }

        if (tags != null && !tags.isEmpty()) {
            predicate = predicate.and(t -> t.getTags().containsAll(tags));
        }

        return latestTasks().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    // ---------------- 3.9 Sorting ----------------
    public List<Task> sortTasks(
            List<Task> tasks,
            String sortBy,
            boolean ascending
    ) {

        Comparator<Task> comparator;

        switch (sortBy.toLowerCase()) {
            case "priority":
                comparator = Comparator.comparing(Task::getPriority);
                break;
            case "duedate":
                comparator = Comparator.comparing(
                        t -> t.getDueDate().orElse(Instant.MAX));
                break;
            case "createddate":
                comparator = Comparator.comparing(Task::getCreatedAt);
                break;
            case "status":
                comparator = Comparator.comparing(Task::getStatus);
                break;
            default:
                comparator = Comparator.comparing(Task::getCreatedAt);
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
