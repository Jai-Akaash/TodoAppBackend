package sandbox;

import enums.*;
import models.*;
import repositories.*;
import services.TaskService;

import java.time.Instant;
import java.util.UUID;

public class TaskServiceRunner {

    public static void main(String[] args) {

        // ---------------- Repositories ----------------
        UserRepository userRepository = new UserRepository();
        TaskRepository taskRepository = new TaskRepository();
        ActivityEventRepository activityRepository = new ActivityEventRepository();

        // ---------------- Service ----------------
        TaskService taskService =
                new TaskService(taskRepository, activityRepository, userRepository);

        // ---------------- Create Users ----------------
        User creator = User.builder()
                .name("Alice")
                .email("alice@test.com")
                .role(Role.MANAGER)
                .build();

        User assignee = User.builder()
                .name("Bob")
                .email("bob@test.com")
                .role(Role.DEVELOPER)
                .build();

        userRepository.save(creator);
        userRepository.save(assignee);

        // ---------------- 1. Create Task ----------------
        Task task = taskService.createTask(
                "Design API",
                "Design task service APIs",
                creator
        );

        System.out.println("Task created:");
        printTask(task);

        // ---------------- 2. Assign Task ----------------
        task = taskService.assignTask(task.getId(), assignee.getId(), creator);
        System.out.println("\nAfter assignment:");
        printTask(task);

        // ---------------- 3. Update Status ----------------
        task = taskService.updateStatus(task.getId(), Status.IN_PROGRESS, assignee);
        System.out.println("\nAfter status update:");
        printTask(task);

        // ---------------- 4. Update Priority ----------------
        task = taskService.updatePriority(task.getId(), Priority.HIGH, creator);
        System.out.println("\nAfter priority update:");
        printTask(task);

        // ---------------- 5. Add Due Date ----------------
        task = taskService.updateDueDate(
                task.getId(),
                Instant.now().plusSeconds(86400),
                creator
        );
        System.out.println("\nAfter due date update:");
        printTask(task);

        // ---------------- 6. Add Comment ----------------
        task = taskService.addComment(
                task.getId(),
                "Looks good, proceeding",
                assignee
        );
        System.out.println("\nAfter comment:");
        printTask(task);

        // ---------------- 7. Complete Task ----------------
        task = taskService.updateStatus(task.getId(), Status.COMPLETED, assignee);
        System.out.println("\nAfter completion:");
        printTask(task);

        // ---------------- 8. View Task History ----------------
        System.out.println("\nTask History (latest first):");
        taskService.viewTaskHistory(task.getId())
                .forEach(t ->
                        System.out.println(
                                "Version " + t.getVersion()
                                        + " | Status=" + t.getStatus()
                                        + " | Priority=" + t.getPriority()
                        )
                );

        // ---------------- 9. View Activity Log ----------------
        System.out.println("\nActivity Log:");
        activityRepository.findByTaskId(task.getId())
                .forEach(e ->
                        System.out.println(
                                e.getTimestamp()
                                        + " | " + e.getActivityType()
                                        + " | " + e.getDetails()
                        )
                );
    }

    // ---------------- Helper ----------------
    private static void printTask(Task task) {
        System.out.println("ID        : " + task.getId());
        System.out.println("Version   : " + task.getVersion());
        System.out.println("Status    : " + task.getStatus());
        System.out.println("Priority  : " + task.getPriority());
        System.out.println("Assigned  : " +
                task.getAssignedTo().map(User::getEmail).orElse("Unassigned"));
        System.out.println("Due Date  : " +
                task.getDueDate().map(Instant::toString).orElse("No deadline"));
        System.out.println("Comments  : " + task.getComments().size());
    }
}
