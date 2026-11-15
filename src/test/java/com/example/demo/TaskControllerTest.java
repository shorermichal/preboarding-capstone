package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldCreateTask() throws Exception {
        Task newTask = new Task(1L, "Learn Spring Boot", false);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        String jsonResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(1L, "Task to find", false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(jsonResponse, Task.class);

        mockMvc.perform(get("/tasks/" + createdTask.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task to find"));
    }

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        Task invalidTask = new Task(1L, "   ", false);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        String jsonResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(1L, "Task to delete", false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(jsonResponse, Task.class);

        mockMvc.perform(delete("/tasks/" + createdTask.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/" + createdTask.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        String jsonResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(1L, "Initial Task", false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(jsonResponse, Task.class);

        Task updateRequest = new Task(createdTask.id(), "Updated Task", true);

        mockMvc.perform(put("/tasks/" + createdTask.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTask.id()))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.done").value(true));

        mockMvc.perform(get("/tasks/" + createdTask.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentTask() throws Exception {
        Task updateRequest = new Task(999L, "Non-existent Task", true);

        mockMvc.perform(put("/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenUpdatingTaskWithBlankTitle() throws Exception {
        String jsonResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(1L, "Valid Task", false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(jsonResponse, Task.class);

        Task invalidUpdate = new Task(createdTask.id(), "   ", true);

        mockMvc.perform(put("/tasks/" + createdTask.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterTasksByDoneTrue() throws Exception {
        String resDone = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(1L, "Completed Filter Test", true))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Task doneTask = objectMapper.readValue(resDone, Task.class);

        String resPending = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(2L, "Pending Filter Test", false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Task pendingTask = objectMapper.readValue(resPending, Task.class);

        mockMvc.perform(get("/tasks").param("done", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + doneTask.id() + ")].title").value(hasItem("Completed Filter Test")))
                .andExpect(jsonPath("$[?(@.id == " + pendingTask.id() + ")]").value(empty()));
    }

    @Test
    void shouldFilterTasksByDoneFalse() throws Exception {
        String resDone = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(1L, "Completed Filter Test 2", true))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Task doneTask = objectMapper.readValue(resDone, Task.class);

        String resPending = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(2L, "Pending Filter Test 2", false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Task pendingTask = objectMapper.readValue(resPending, Task.class);

        mockMvc.perform(get("/tasks").param("done", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + pendingTask.id() + ")].title").value(hasItem("Pending Filter Test 2")))
                .andExpect(jsonPath("$[?(@.id == " + doneTask.id() + ")]").value(empty()));
    }

    @Test
    void shouldReturnAllTasksWhenNoFilterProvided() throws Exception {
        String resDone = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(1L, "All Filter Completed", true))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Task doneTask = objectMapper.readValue(resDone, Task.class);

        String resPending = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(2L, "All Filter Incomplete", false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Task pendingTask = objectMapper.readValue(resPending, Task.class);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + doneTask.id() + ")].title").value(hasItem("All Filter Completed")))
                .andExpect(jsonPath("$[?(@.id == " + pendingTask.id() + ")].title").value(hasItem("All Filter Incomplete")));
    }
}
