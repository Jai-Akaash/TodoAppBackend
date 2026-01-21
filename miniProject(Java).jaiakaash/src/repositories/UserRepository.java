package repositories;

import models.User;

import java.util.*;
import java.util.stream.Collectors;

public class UserRepository {

    private final List<User> users = new ArrayList<>();


    public User save(User user) {
        deleteById(user.getId());
        users.add(user);
        return user;
    }

    public Optional<User> findById(UUID id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public void deleteById(UUID id) {
        users.removeIf(u -> u.getId().equals(id));
    }
}
