---
name: add-endpoint
description: Adds a new REST endpoint to this Spring Boot project with tests. Use when asked to add or change an API endpoint.
---

# Add an endpoint

1. Read the existing controller and tests to match conventions.
2. Write a MockMvc test for the new endpoint first, including error cases.
3. Implement the endpoint. Keep business logic out of the controller.
4. Run ./mvnw test (or .\mvnw.cmd test on Windows) and fix failures until all pass.
5. Summarize the change and list the files you touched.
6. Preserve existing API behavior and avoid unrelated code changes.