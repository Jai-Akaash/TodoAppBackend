package services;

import enums.ActivityType;
import enums.Priority;
import enums.Status;
import models.ActivityEvent;
import models.Comment;
import models.Task;
import models.User;
import repositories.ActivityEventRepository;
import repositories.TaskRepository;
import repositories.UserRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class TaskService {

    private final TaskRepository taskRepository;
    private final ActivityEventRepository activityRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       ActivityEventRepository activityRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    // ---------------- 2.1 Create Task ----------------
    public Task createTask(String title, String description, User creator) {

        Task task = Task.builder()
                .title(title)
                .description(description)
                .createdBy(creator)
                .build();

        taskRepository.save(task);

        recordActivity(task.getId(), ActivityType.TASK_CREATED, creator, null);

        return task;
    }

    // ---------------- 2.2 View Task (latest) ----------------
    public Task viewTask(UUID taskId) {
        return taskRepository.findLatestById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));
    }

    // ---------------- 2.3 Update Status ----------------
    private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS =
            new HashMap<Status, Set<Status>>() {{
                put(Status.OPEN, Set.of(Status.IN_PROGRESS, Status.CANCELLED));
                put(Status.IN_PROGRESS, Set.of(Status.COMPLETED, Status.CANCELLED));
                put(Status.COMPLETED, Set.of());
                put(Status.CANCELLED, Set.of());
            }};

    public Task updateStatus(UUID taskId, Status newStatus, User actor) {

        Task current = viewTask(taskId);

        if (!ALLOWED_TRANSITIONS.get(current.getStatus()).contains(newStatus)) {
            throw new IllegalStateException("Invalid status transition");
        }

        Task updated = cloneTask(current)
                .status(newStatus)
                .version(current.getVersion() + 1)
                .updatedAt(Instant.now())
                .build();

        taskRepository.save(updated);

        recordActivity(taskId, ActivityType.STATUS_CHANGED, actor,
                current.getStatus() + " -> " + newStatus);

        return updated;
    }

    // ---------------- 2.4 Assign / Unassign ----------------
    public Task assignTask(UUID taskId, UUID userId, User actor) {

        Task current = viewTask(taskId);

        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Task updated = cloneTask(current)
                .assignedTo(assignee)
                .version(current.getVersion() + 1)
                .updatedAt(Instant.now())
                .build();

        taskRepository.save(updated);

        recordActivity(taskId, ActivityType.ASSIGNEE_CHANGED, actor,
                "Assigned to " + assignee.getEmail());

        return updated;
    }

    public Task unassignTask(UUID taskId, User actor) {

        Task current = viewTask(taskId);

        Task updated = cloneTask(current)
                .assignedTo(null)
                .version(current.getVersion() + 1)
                .updatedAt(Instant.now())
                .build();

        taskRepository.save(updated);

        recordActivity(taskId, ActivityType.ASSIGNEE_CHANGED, actor, "Unassigned");

        return updated;
    }

    // ---------------- 2.5 Update Priority ----------------
    public Task updatePriority(UUID taskId, Priority priority, User actor) {

        Task current = viewTask(taskId);

        Task updated = cloneTask(current)
                .priority(priority)
                .version(current.getVersion() + 1)
                .updatedAt(Instant.now())
                .build();

        taskRepository.save(updated);

        recordActivity(taskId, ActivityType.PRIORITY_CHANGED, actor,
                current.getPriority() + " -> " + priority);

        return updated;
    }

    // ---------------- 2.6 Update Due Date ----------------
    public Task updateDueDate(UUID taskId, Instant dueDate, User actor) {

        Task current = viewTask(taskId);

        Task updated = cloneTask(current)
                .dueDate(dueDate)
                .version(current.getVersion() + 1)
                .updatedAt(Instant.now())
                .build();

        taskRepository.save(updated);

        recordActivity(taskId, ActivityType.DUE_DATE_CHANGED, actor,
                dueDate == null ? "Deadline removed" : dueDate.toString());

        return updated;
    }

    // ---------------- 2.7 Add Comment ----------------
    public Task addComment(UUID taskId, String text, User author) {

        Task current = viewTask(taskId);

        Comment comment = Comment.builder()
                .author(author)
                .message(text)
                .build();

        List<Comment> comments = new ArrayList<>(current.getComments());
        comments.add(comment);

        Task updated = cloneTask(current)
                .comments(comments)
                .version(current.getVersion() + 1)
                .updatedAt(Instant.now())
                .build();

        taskRepository.save(updated);

        recordActivity(taskId, ActivityType.COMMENT_ADDED, author, text);

        return updated;
    }

    // ---------------- 2.8 View Task History ----------------
    public List<Task> viewTaskHistory(UUID taskId) {

        return taskRepository.findAllVersions(taskId)
                .stream()
                .sorted(Comparator.comparingInt(Task::getVersion).reversed())
                .collect(Collectors.toList());
    }

    // ---------------- Helpers ----------------
    private Task.Builder cloneTask(Task t) {
        return Task.builder()
                .id(t.getId())
                .version(t.getVersion())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
                .createdBy(t.getCreatedBy())
                .assignedTo(t.getAssignedTo().orElse(null))
                .dueDate(t.getDueDate().orElse(null))
                .tags(t.getTags())
                .comments(t.getComments())
                .createdAt(t.getCreatedAt());
    }

    private void recordActivity(UUID taskId,
                                ActivityType type,
                                User actor,
                                String details) {

        ActivityEvent event = ActivityEvent.builder()
                .taskId(taskId)
                .activityType(type)
                .performedBy(actor)
                .details(details)
                .build();

        activityRepository.save(event);
    }
}
