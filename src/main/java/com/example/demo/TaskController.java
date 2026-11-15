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
    public List<Task> getAllTasks(@RequestParam(required = false) Boolean done) {
        if (done == null) {
            return tasks;
        }
        return tasks.stream()
                .filter(t -> t.done() == done)
                .toList();
    }

    // GET /tasks/count
    @GetMapping("/count")
    public TaskCount getTaskCount() {
        return TaskCount.from(tasks);
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

    // PUT /tasks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task updatedTask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id() == id) {
                Task task = new Task(id, updatedTask.title(), updatedTask.done());
                tasks.set(i, task);
                return ResponseEntity.ok(task);
            }
        }
        return ResponseEntity.notFound().build();
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
