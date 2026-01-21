package sandbox;

import enums.Priority;
import enums.Role;
import enums.Status;
import models.Task;
import models.User;
import repositories.ActivityEventRepository;
import repositories.TaskRepository;
import repositories.UserRepository;
import services.TaskSearchService;
import services.TaskService;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskSearchServiceRunner {

    public static void main(String[] args) {

        // ---------------- Repositories ----------------
        UserRepository userRepository = new UserRepository();
        TaskRepository taskRepository = new TaskRepository();
        ActivityEventRepository activityRepository = new ActivityEventRepository();

        // ---------------- Services ----------------
        TaskService taskService =
                new TaskService(taskRepository, activityRepository, userRepository);
        TaskSearchService searchService =
                new TaskSearchService(taskRepository);

        // ---------------- Users ----------------
        User alice = User.builder()
                .name("Alice")
                .email("alice@test.com")
                .role(Role.MANAGER)
                .build();

        User bob = User.builder()
                .name("Bob")
                .email("bob@test.com")
                .role(Role.DEVELOPER)
                .build();

        userRepository.save(alice);
        userRepository.save(bob);

        // ---------------- Create Tasks ----------------
        Task t1 = taskService.createTask(
                "Fix security bug",
                "Fix auth vulnerability",
                alice
        );

        Task t2 = taskService.createTask(
                "Refactor module",
                "Cleanup legacy code",
                alice
        );

        Task t3 = taskService.createTask(
                "Deploy release",
                "Deploy to production",
                bob
        );

        // Assign tasks
        t1 = taskService.assignTask(t1.getId(), bob.getId(), alice);
        t2 = taskService.assignTask(t2.getId(), bob.getId(), alice);

        // Update priority
        t1 = taskService.updatePriority(t1.getId(), Priority.CRITICAL, alice);
        t2 = taskService.updatePriority(t2.getId(), Priority.HIGH, alice);

        // Update status
        taskService.updateStatus(t1.getId(), Status.IN_PROGRESS, bob);
        taskService.updateStatus(t2.getId(), Status.IN_PROGRESS, bob);
        taskService.updateStatus(t2.getId(), Status.COMPLETED, bob);


        // Add due dates
        taskService.updateDueDate(
                t1.getId(),
                Instant.now().minusSeconds(86400 * 2), // overdue
                alice
        );

        taskService.updateDueDate(
                t3.getId(),
                Instant.now().plusSeconds(86400 * 3),
                bob
        );

        // ---------------- SEARCH TESTS ----------------

        // 3.1 Filter by Status
        System.out.println("\n--- Tasks with OPEN or IN_PROGRESS ---");
        searchService.filterByStatus(Set.of(Status.OPEN, Status.IN_PROGRESS))
                .forEach(t -> print(t));

        // 3.2 Filter by Priority
        System.out.println("\n--- Tasks with HIGH or CRITICAL priority ---");
        searchService.filterByPriority(Set.of(Priority.HIGH, Priority.CRITICAL))
                .forEach(t -> print(t));

        // 3.3 Filter by Assignee (grouped)
        System.out.println("\n--- Tasks grouped by assignee ---");
        Map<User, List<Task>> byAssignee = searchService.filterByAssignee();
        byAssignee.forEach((user, tasks) -> {
            System.out.println(user.getEmail());
            tasks.forEach(t -> System.out.println("  - " + t.getTitle()));
        });

        // 3.4 Filter by Creator
        System.out.println("\n--- Tasks created by Alice ---");
        searchService.filterByCreator(alice)
                .forEach(t -> print(t));

        // 3.5 Overdue Tasks
        System.out.println("\n--- Overdue tasks ---");
        searchService.findOverdueTasks()
                .forEach((task, days) ->
                        System.out.println(task.getTitle() + " overdue by " + days + " days")
                );

        // 3.6 Date Range
        System.out.println("\n--- Tasks created today ---");
        searchService.createdBetween(
                Instant.now().minusSeconds(86400),
                Instant.now()
        ).forEach(t -> print(t));

        // 3.8 Combined Filter
        System.out.println("\n--- Combined filter: HIGH+, assigned to Bob, overdue ---");
        searchService.combinedFilter(
                        Set.of(Status.IN_PROGRESS),
                        Set.of(Priority.HIGH, Priority.CRITICAL),
                        bob,
                        true,
                        null
                )
                .forEach(t -> print(t));

        // 3.9 Sorting
        System.out.println("\n--- Sorted by priority DESC ---");
        searchService.sortTasks(
                        searchService.filterByPriority(
                                Set.of(Priority.HIGH, Priority.CRITICAL)),
                        "priority",
                        false
                )
                .forEach(t -> print(t));
    }

    // ---------------- Helper ----------------
    private static void print(Task t) {
        System.out.println(
                t.getTitle()
                        + " | Status=" + t.getStatus()
                        + " | Priority=" + t.getPriority()
                        + " | Assigned=" +
                        t.getAssignedTo().map(User::getEmail).orElse("Unassigned")
        );
    }
}
