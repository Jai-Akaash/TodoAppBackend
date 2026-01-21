package models;

import enums.Priority;
import enums.Status;
import models.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class Task {

    // --- Core fields ---
    private final UUID id;
    private final int version; // starts at 1
    private final String title;
    private final String description;
    private final Status status;
    private final Priority priority;
    private final User createdBy;
    private final Optional<User> assignedTo;
    private final Optional<Instant> dueDate;
    private final List<String> tags;
    private final List<Comment> comments;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Task(Builder b) {
        this.id = b.id;
        this.version = b.version;
        this.title = b.title;
        this.description = b.description;
        this.status = b.status;
        this.priority = b.priority;
        this.createdBy = b.createdBy;
        this.assignedTo = Optional.ofNullable(b.assignedTo);
        this.dueDate = Optional.ofNullable(b.dueDate);
        this.tags = Collections.unmodifiableList(new ArrayList<>(b.tags));
        this.comments = Collections.unmodifiableList(new ArrayList<>(b.comments));
        this.createdAt = b.createdAt;
        this.updatedAt = b.updatedAt;
    }

    // ---------- Getters ----------
    public UUID getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Optional<User> getAssignedTo() {
        return assignedTo;
    }

    public Optional<Instant> getDueDate() {
        return dueDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ---------- Builder ----------
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private int version = 1;
        private String title;
        private String description;
        private Status status = Status.OPEN;
        private Priority priority = Priority.MEDIUM;
        private User createdBy;
        private User assignedTo;
        private Instant dueDate;
        private final List<String> tags = new ArrayList<>();
        private final List<Comment> comments = new ArrayList<>();
        private Instant createdAt;
        private Instant updatedAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder assignedTo(User assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public Builder dueDate(Instant dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder tags(Collection<String> tags) {
            if (tags != null) this.tags.addAll(tags);
            return this;
        }

        public Builder comments(Collection<Comment> comments) {
            if (comments != null) this.comments.addAll(comments);
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Task build() {
//            Objects.requireNonNull(title, "title is required");
//            Objects.requireNonNull(description, "description is required");
//            Objects.requireNonNull(createdBy, "createdBy is required");

            if (id == null) id = UUID.randomUUID();
            if (createdAt == null) createdAt = Instant.now();
            if (updatedAt == null) updatedAt = createdAt;
            if (version < 1) version = 1;
//            Objects.requireNonNull(status, "status must not be null");
//            Objects.requireNonNull(priority, "priority must not be null");

            // normalize tags (lowercase unique)


            // NEED TO CODE

            return new Task(this);
        }
    }
}