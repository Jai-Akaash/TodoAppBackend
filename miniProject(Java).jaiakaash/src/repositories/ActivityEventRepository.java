package repositories;

import models.ActivityEvent;

import java.util.*;
import java.util.stream.Collectors;

public class ActivityEventRepository {

    private final List<ActivityEvent> events = new ArrayList<>();

    public ActivityEvent save(ActivityEvent event) {
        events.add(event);
        return event;
    }

    public List<ActivityEvent> findByTaskId(UUID taskId) {
        return events.stream()
                .filter(e -> e.getTaskId().equals(taskId))
                .sorted(Comparator.comparing(ActivityEvent::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<ActivityEvent> findAll() {
        return new ArrayList<>(events);
    }
}
