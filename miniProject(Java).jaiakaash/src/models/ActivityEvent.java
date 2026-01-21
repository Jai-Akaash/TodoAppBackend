package models;



import enums.ActivityType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ActivityEvent {

    private final UUID eventId;
    private final UUID taskId;
    private final ActivityType activityType;
    private final User performedBy;
    private final Instant timestamp;
    private final String details; // optional

    private ActivityEvent(Builder builder) {
        this.eventId = builder.eventId;
        this.taskId = builder.taskId;
        this.activityType = builder.activityType;
        this.performedBy = builder.performedBy;
        this.timestamp = builder.timestamp;
        this.details = builder.details;
    }

    // ---------- Getters ----------
    public UUID getEventId() {
        return eventId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }

    // ---------- Builder ----------
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private UUID eventId;
        private UUID taskId;
        private ActivityType activityType;
        private User performedBy;
        private Instant timestamp;
        private String details;

        private Builder() {}

        public Builder eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder taskId(UUID taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder activityType(ActivityType activityType) {
            this.activityType = activityType;
            return this;
        }

        public Builder performedBy(User performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public ActivityEvent build() {

            // ---- Required validations ----
//            Objects.requireNonNull(taskId, "taskId is required");
//            Objects.requireNonNull(activityType, "activityType is required");
//            Objects.requireNonNull(performedBy, "performedBy is required");

            if (eventId == null) {
                eventId = UUID.randomUUID();
            }

            if (timestamp == null) {
                timestamp = Instant.now();
            }

            return new ActivityEvent(this);
        }
    }
}
