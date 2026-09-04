# API Documentation

## AI Project Management System

### Version

v1

### Base URL

```http
/api/v1
```

---

# Authentication

Tất cả API (trừ login/register) yêu cầu JWT Access Token.

Header:

```http
Authorization: Bearer <access_token>
```

---

# 1. Authentication APIs

## Register

```http
POST /auth/register
```

### Request

```json
{
  "email": "user@example.com",
  "password": "123456",
  "fullName": "Nguyen Van A"
}
```

### Response

```json
{
  "message": "Register successfully"
}
```

---

## Login

```http
POST /auth/login
```

### Request

```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

### Response

```json
{
  "accessToken": "jwt_token",
  "refreshToken": "refresh_token"
}
```

---

## Refresh Token

```http
POST /auth/refresh-token
```

---

## Logout

```http
POST /auth/logout
```

---

## Current User

```http
GET /me
```

---

## Update Profile

```http
PUT /me
```

---

## Change Password

```http
PUT /me/password
```

---

# 2. User APIs

## Get Users

```http
GET /users
```

Query Params

```http
?page=0
&size=20
&keyword=nam
```

---

## Get User By Id

```http
GET /users/{id}
```

---

## Create User

```http
POST /users
```

---

## Update User

```http
PUT /users/{id}
```

---

## Delete User

```http
DELETE /users/{id}
```

---

## Search Users

```http
GET /users/search
```

---

# 3. Skill APIs

## Get Skills

```http
GET /skills
```

---

## Create Skill

```http
POST /skills
```

### Request

```json
{
  "name": "Spring Boot"
}
```

---

## Update Skill

```http
PUT /skills/{id}
```

---

## Delete Skill

```http
DELETE /skills/{id}
```

---

# 4. User Skill APIs

## Get User Skills

```http
GET /users/{userId}/skills
```

---

## Add User Skill

```http
POST /users/{userId}/skills
```

### Request

```json
{
  "skillId": 1,
  "level": 5
}
```

---

## Update User Skill

```http
PUT /users/{userId}/skills/{skillId}
```

---

## Delete User Skill

```http
DELETE /users/{userId}/skills/{skillId}
```

---

# 5. Project APIs

## Get Projects

```http
GET /projects
```

---

## Get Project Detail

```http
GET /projects/{id}
```

---

## Create Project

```http
POST /projects
```

### Request

```json
{
  "name": "Project Management System",
  "description": "Graduation Project",
  "startDate": "2026-01-01",
  "endDate": "2026-06-01"
}
```

---

## Update Project

```http
PUT /projects/{id}
```

---

## Delete Project

```http
DELETE /projects/{id}
```

---

## Project Overview

```http
GET /projects/{id}/overview
```

### Response

```json
{
  "totalTasks": 120,
  "completedTasks": 82,
  "completionRate": 68
}
```

---

# 6. Project Member APIs

## Get Members

```http
GET /projects/{projectId}/members
```

---

## Add Member

```http
POST /projects/{projectId}/members
```

### Request

```json
{
  "userId": "uuid"
}
```

---

## Remove Member

```http
DELETE /projects/{projectId}/members/{memberId}
```

---

# 7. Project Role APIs

## Get Project Roles

```http
GET /project-roles
```

---

## Assign Role

```http
POST /projects/{projectId}/members/{memberId}/roles
```

### Request

```json
{
  "roleId": 1
}
```

---

## Remove Role

```http
DELETE /projects/{projectId}/members/{memberId}/roles/{roleId}
```

---

# 8. Sprint APIs

## Get Sprints

```http
GET /projects/{projectId}/sprints
```

---

## Get Sprint Detail

```http
GET /sprints/{id}
```

---

## Create Sprint

```http
POST /projects/{projectId}/sprints
```

### Request

```json
{
  "name": "Sprint 1",
  "goal": "Implement Authentication Module",
  "startDate": "2026-01-01",
  "endDate": "2026-01-14"
}
```

---

## Update Sprint

```http
PUT /sprints/{id}
```

---

## Delete Sprint

```http
DELETE /sprints/{id}
```

---

## Start Sprint

```http
POST /sprints/{id}/start
```

---

## Complete Sprint

```http
POST /sprints/{id}/complete
```

---

## Sprint Metrics

```http
GET /sprints/{id}/metrics
```

---

# 9. Task APIs

## Get Task Detail

```http
GET /tasks/{id}
```

---

## Create Task

```http
POST /tasks
```

### Request

```json
{
  "projectId": "uuid",
  "sprintId": "uuid",
  "title": "Implement Login API",
  "description": "Build JWT Authentication",
  "priority": "HIGH",
  "storyPoint": 5
}
```

---

## Update Task

```http
PUT /tasks/{id}
```

---

## Delete Task

```http
DELETE /tasks/{id}
```

---

## Search Tasks

```http
GET /tasks
```

Query Params

```http
?projectId=
&sprintId=
&status=
&priority=
&assignee=
```

---

## Update Task Status

```http
PATCH /tasks/{id}/status
```

### Request

```json
{
  "status": "IN_PROGRESS"
}
```

---

## Update Priority

```http
PATCH /tasks/{id}/priority
```

---

## Move Task To Sprint

```http
PATCH /tasks/{id}/sprint
```

---

# 10. Kanban APIs

## Get Board

```http
GET /projects/{projectId}/board
```

### Response

```json
{
  "todo": [],
  "inProgress": [],
  "testing": [],
  "done": []
}
```

---

## Move Task

```http
PATCH /tasks/{id}/move
```

### Request

```json
{
  "status": "TESTING"
}
```

---

# 11. Task Assignment APIs

## Get Assignees

```http
GET /tasks/{taskId}/assignees
```

---

## Assign User

```http
POST /tasks/{taskId}/assignees
```

### Request

```json
{
  "userId": "uuid"
}
```

---

## Remove Assignee

```http
DELETE /tasks/{taskId}/assignees/{userId}
```

---

# 12. Comment APIs

## Get Comments

```http
GET /tasks/{taskId}/comments
```

---

## Create Comment

```http
POST /tasks/{taskId}/comments
```

### Request

```json
{
  "content": "Authentication API completed."
}
```

---

## Update Comment

```http
PUT /comments/{id}
```

---

## Delete Comment

```http
DELETE /comments/{id}
```

---

# 13. Attachment APIs

## Upload File

```http
POST /tasks/{taskId}/attachments
```

Content-Type

```http
multipart/form-data
```

---

## Download File

```http
GET /attachments/{id}
```

---

## Delete File

```http
DELETE /attachments/{id}
```

---

# 14. Task History APIs

## Get Task History

```http
GET /tasks/{taskId}/histories
```

---

# 15. Notification APIs

## Get Notifications

```http
GET /notifications
```

---

## Mark As Read

```http
PATCH /notifications/{id}/read
```

---

## Mark All As Read

```http
PATCH /notifications/read-all
```

---

# 16. Dashboard APIs

## Project Dashboard

```http
GET /projects/{projectId}/dashboard
```

---

## Burndown Chart

```http
GET /projects/{projectId}/burndown
```

---

## Velocity Chart

```http
GET /projects/{projectId}/velocity
```

---

## Team Workload

```http
GET /projects/{projectId}/workload
```

---

# 17. AI APIs

## AI Task Assignment

```http
POST /ai/task-assignment
```

### Request

```json
{
  "taskId": "uuid"
}
```

### Response

```json
{
  "userId": "uuid",
  "userName": "Nguyen Van A",
  "score": 0.92
}
```

---

## AI Sprint Prediction

```http
GET /ai/sprints/{sprintId}/prediction
```

### Response

```json
{
  "completionProbability": 87,
  "riskLevel": "MEDIUM"
}
```

---

## AI Project Summary

```http
GET /ai/projects/{projectId}/summary
```

### Response

```json
{
  "summary": "Sprint 3 is currently 70% completed..."
}
```

---

# API Statistics

| Module          | API Count |
| --------------- | --------- |
| Authentication  | 6         |
| Users           | 6         |
| Skills          | 4         |
| User Skills     | 4         |
| Projects        | 6         |
| Project Members | 3         |
| Project Roles   | 3         |
| Sprints         | 8         |
| Tasks           | 8         |
| Kanban          | 2         |
| Assignments     | 3         |
| Comments        | 4         |
| Attachments     | 3         |
| Notifications   | 3         |
| Dashboard       | 4         |
| AI              | 3         |

## Total

Approximately 65 APIs

This API design follows RESTful conventions and supports Agile/Scrum project management, Kanban workflow, role-based access control, dashboard analytics, and AI-powered project management features.
