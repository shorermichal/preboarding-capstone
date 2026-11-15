package com.example.demo;

import java.util.List;

public record TaskCount(long total, long done) {
    public static TaskCount from(List<Task> tasks) {
        long doneCount = tasks.stream().filter(Task::done).count();
        return new TaskCount(tasks.size(), doneCount);
    }
}
