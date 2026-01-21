package sandbox;
import enums.Role;
import enums.Status;
import models.Task;
import models.User;
import repositories.UserRepository;
import services.UserService;

import java.util.*;

public class UserServiceRunner {

    public static void main(String[] args) {

        // --- Setup ---
        UserRepository userRepository = new UserRepository();
        List<Task> tasks = new ArrayList<>(); // no DB
        UserService userService = new UserService(userRepository, tasks);

        // --- 1. Create Users ---
        User u1 = userService.createUser("Alice", "alice@gmail.com", Role.ADMIN);
        User u2 = userService.createUser("Bob", "bob@gmail.com", Role.ADMIN);

        System.out.println("Created Users:");
        System.out.println(u1.getId() + " " + u1.getName() + " " + u1.isActive());
        System.out.println(u2.getId() + " " + u2.getName() + " " + u2.isActive());

        // --- 2. View User ---
        User fetched = userService.getUserById(u1.getId());
        System.out.println("\nFetched User:");
        System.out.println(fetched.getEmail());

        // --- 3. List Users ---
        System.out.println("\nList Users (sorted by name):");
        userService.listUsers("name")
                .forEach(u -> System.out.println(u.getName()));

        // --- 4. Update User ---
        User updated = userService.updateUser(u1.getId(), "Alice Updated", Role.MANAGER);
        System.out.println("\nUpdated User:");
        System.out.println(updated.getName() + " " + updated.getRole());

        // --- 5. Assign active task to Bob ---
        Task task = Task.builder()
                .title("Test Task")
                .description("Task desc")
                .createdBy(u1)
                .assignedTo(u2)
                .status(Status.OPEN)
                .build();

        tasks.add(task);

        // --- 6. Try deleting Bob (should FAIL) ---
        try {
            userService.deleteUser(u2.getId());
        } catch (Exception e) {
            System.out.println("\nDelete Bob failed as expected:");
            System.out.println(e.getMessage());
        }

        // --- 7. Complete task ---
        Task completedTask = Task.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .createdBy(task.getCreatedBy())
                .assignedTo(task.getAssignedTo().orElse(null))
                .status(Status.COMPLETED)
                .build();

        tasks.clear();
        tasks.add(completedTask);

        // --- 8. Delete Bob again (should PASS) ---
        User deleted = userService.deleteUser(u2.getId());
        System.out.println("\nBob deleted (soft delete):");
        System.out.println("Active = " + deleted.isActive());
    }
}
