package models;
import java.time.Instant;
import java.util.*;

public class Comment {
    private final UUID id;
    private final User author;
    private final String message;
    private final Instant createdAt;

    private Comment(CommentBuilder b) {
        this.id = b.id;
        this.author = b.author;
        this.message = b.message;
        this.createdAt = b.createdAt;
    }

    public UUID getId() { return id; }
    public User getAuthor() { return author; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }

    public static CommentBuilder builder() { return new CommentBuilder(); }

    public static final class CommentBuilder {
        private UUID id;
        private User author;
        private String message;
        private Instant createdAt;

        private CommentBuilder() {}

        public CommentBuilder id(UUID id) { this.id = id; return this; }
        public CommentBuilder author(User author) { this.author = author; return this; }
        public CommentBuilder message(String message) { this.message = message; return this; }
        public CommentBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public Comment build() {
//            Objects.requireNonNull(author, "comment author required");
//            Objects.requireNonNull(message, "comment message required");
            if (id == null) id = UUID.randomUUID();
            if (createdAt == null) createdAt = Instant.now();
            return new Comment(this);
        }
    }
}

