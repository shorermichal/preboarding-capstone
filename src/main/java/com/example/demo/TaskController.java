package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final List<Task> tasks = new ArrayList<>();
    private long nextId = 1;

    // GET /tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return tasks;
    }

    // GET /tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        for (Task t : tasks) {
            if (t.id() == id) {
                return ResponseEntity.ok(t);
            }
        }
        return ResponseEntity.notFound().build();
    }
    // POST /tasks
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task) {
        Task newTask = new Task(nextId++, task.title(), task.done());
        tasks.add(newTask);
        return newTask;
    }

    // DELETE /tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean removed = tasks.removeIf(t -> t.id() == id);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}