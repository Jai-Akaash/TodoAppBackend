package models;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import enums.Role;

public final class User {

    private final UUID id;
    private final String name;
    private final String email;
    private final Role role;
    private final Instant createdAt;
    private final boolean active;

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.role = builder.role;
        this.createdAt = builder.createdAt;
        this.active = builder.active;
    }

    // ---------- Getters  ----------

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    // ---------- Business-safe updates ----------
    // Name & Role CAN be updated -> return NEW instance
    public User updateNameAndRole(String newName, Role newRole) {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .createdAt(this.createdAt)
                .active(this.active)
                .name(newName)
                .role(newRole)
                .build();
    }



    // ---------- Builder ----------
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private UUID id;
        private String name;
        private String email;
        private Role role;
        private Instant createdAt;
        private boolean active = true;

//        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public User build() {

            // ---- Validation ----
//            Objects.requireNonNull(name, "Name cannot be null");
//            Objects.requireNonNull(email, "Email cannot be null");
//            Objects.requireNonNull(role, "Role cannot be null");

            if (id == null) {
                id = UUID.randomUUID();
            }

            if (createdAt == null) {
                createdAt = Instant.now();
            }

            return new User(this);
        }
    }
}
