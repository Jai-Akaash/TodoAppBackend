package services;

import enums.Role;
import enums.Status;
import models.Task;
import models.User;
import repositories.UserRepository;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository userRepository;
    private final List<Task> tasks;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public UserService(UserRepository userRepository, List<Task> tasks) {
        this.userRepository = userRepository;
        this.tasks = tasks;
    }

    // 1.1 Create User
    public User createUser(String name, String email, Role role) {
        User user = User.builder()
                .name(name)
                .email(email)
                .role(role)
                .build();

        return userRepository.save(user);
    }

    // 1.2 View User
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    // 1.3 List All Users with sorting
    public List<User> listUsers(String sortBy) {

        Comparator<User> comparator = switch (sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER);
            case "email" -> Comparator.comparing(User::getEmail, String.CASE_INSENSITIVE_ORDER);
            case "role" -> Comparator.comparing(u -> u.getRole().name());
            default -> Comparator.comparing(User::getCreatedAt);
        };

        List<User> users = userRepository.findAll()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        System.out.println("Total users: " + users.size());
        return users;
    }

    // 1.4 Update User
    public User updateUser(UUID userId, String newName, Role newRole) {

        User existing = getUserById(userId);

        User updated = existing.updateNameAndRole(
                newName != null ? newName : existing.getName(),
                newRole != null ? newRole : existing.getRole()
        );

        return userRepository.save(updated);
    }

    // 1.5 Soft Delete User
    public User deleteUser(UUID userId) {

        User user = getUserById(userId);

        boolean hasActiveTasks = tasks.stream()
                .filter(t -> t.getAssignedTo().isPresent())
                .anyMatch(t ->
                        t.getAssignedTo().get().getId().equals(userId)
                                && (t.getStatus() == Status.OPEN
                                || t.getStatus() == Status.IN_PROGRESS)
                );

        if (hasActiveTasks) {
            throw new IllegalStateException("User has active assigned tasks");
        }

        User inactiveUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .active(false)
                .build();

        return userRepository.save(inactiveUser);
    }

}
