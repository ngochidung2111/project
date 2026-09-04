package com.example.jira_clone.config;

import com.example.jira_clone.entity.*;
import com.example.jira_clone.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectMemberRoleRepository projectMemberRoleRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final CommentRepository commentRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            SkillRepository skillRepository,
            UserSkillRepository userSkillRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            ProjectRoleRepository projectRoleRepository,
            ProjectMemberRoleRepository projectMemberRoleRepository,
            SprintRepository sprintRepository,
            TaskRepository taskRepository,
            TaskAssignmentRepository taskAssignmentRepository,
            CommentRepository commentRepository,
            TaskHistoryRepository taskHistoryRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRoleRepository = projectRoleRepository;
        this.projectMemberRoleRepository = projectMemberRoleRepository;
        this.sprintRepository = sprintRepository;
        this.taskRepository = taskRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.commentRepository = commentRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Checking database seeding status...");

        // 1. Seed System Roles
        Role adminRole = roleRepository.findByCode("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .code("ADMIN")
                        .description("System Administrator")
                        .build()));

        Role userRole = roleRepository.findByCode("USER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .code("USER")
                        .description("Default project member")
                        .build()));

        // 2. Seed Project Roles
        ProjectRole ownerProjRole = projectRoleRepository.findByCode("OWNER")
                .orElseGet(() -> projectRoleRepository.save(ProjectRole.builder()
                        .code("OWNER")
                        .name("Project Owner")
                        .build()));

        ProjectRole leadProjRole = projectRoleRepository.findByCode("LEAD")
                .orElseGet(() -> projectRoleRepository.save(ProjectRole.builder()
                        .code("LEAD")
                        .name("Tech Lead")
                        .build()));

        ProjectRole devProjRole = projectRoleRepository.findByCode("DEVELOPER")
                .orElseGet(() -> projectRoleRepository.save(ProjectRole.builder()
                        .code("DEVELOPER")
                        .name("Developer")
                        .build()));

        ProjectRole qaProjRole = projectRoleRepository.findByCode("QA")
                .orElseGet(() -> projectRoleRepository.save(ProjectRole.builder()
                        .code("QA")
                        .name("Quality Assurance")
                        .build()));

        // 3. Seed Skills
        String[] skillNames = {"Java", "Spring Boot", "React", "PostgreSQL", "Docker", "Git", "System Design", "Testing"};
        List<Skill> skills = new ArrayList<>();
        for (String name : skillNames) {
            Skill skill = skillRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> skillRepository.save(Skill.builder().name(name).build()));
            skills.add(skill);
        }

        // 4. Seed Users and related data if no users exist
        if (userRepository.count() == 2) {
            System.out.println("No users found. Seeding default users, projects, sprints, and tasks...");

            String encodedPassword = passwordEncoder.encode("123456");

            // Create Users
            User admin = userRepository.save(User.builder()
                    .email("admin@example.com")
                    .passwordHash(encodedPassword)
                    .fullName("System Admin")
                    .status("ACTIVE")
                    .build());

            User pm = userRepository.save(User.builder()
                    .email("pm@example.com")
                    .passwordHash(encodedPassword)
                    .fullName("Alice PM")
                    .status("ACTIVE")
                    .build());

            User lead = userRepository.save(User.builder()
                    .email("lead@example.com")
                    .passwordHash(encodedPassword)
                    .fullName("Bob Lead")
                    .status("ACTIVE")
                    .build());

            User dev1 = userRepository.save(User.builder()
                    .email("dev1@example.com")
                    .passwordHash(encodedPassword)
                    .fullName("Charlie Dev")
                    .status("ACTIVE")
                    .build());

            User dev2 = userRepository.save(User.builder()
                    .email("dev2@example.com")
                    .passwordHash(encodedPassword)
                    .fullName("David Dev")
                    .status("ACTIVE")
                    .build());

            User qa = userRepository.save(User.builder()
                    .email("qa@example.com")
                    .passwordHash(encodedPassword)
                    .fullName("Eve QA")
                    .status("ACTIVE")
                    .build());

            // Associate System Roles
            userRoleRepository.save(UserRole.builder().user(admin).role(adminRole).build());
            userRoleRepository.save(UserRole.builder().user(admin).role(userRole).build());
            userRoleRepository.save(UserRole.builder().user(pm).role(userRole).build());
            userRoleRepository.save(UserRole.builder().user(lead).role(userRole).build());
            userRoleRepository.save(UserRole.builder().user(dev1).role(userRole).build());
            userRoleRepository.save(UserRole.builder().user(dev2).role(userRole).build());
            userRoleRepository.save(UserRole.builder().user(qa).role(userRole).build());

            // Seed User Skills
            // Charlie Dev (dev1) has Java level 5, Spring Boot level 4
            userSkillRepository.save(UserSkill.builder().user(dev1).skill(skills.get(0)).level(5).build()); // Java
            userSkillRepository.save(UserSkill.builder().user(dev1).skill(skills.get(1)).level(4).build()); // Spring Boot
            // David Dev (dev2) has React level 5, Git level 4
            userSkillRepository.save(UserSkill.builder().user(dev2).skill(skills.get(2)).level(5).build()); // React
            userSkillRepository.save(UserSkill.builder().user(dev2).skill(skills.get(5)).level(4).build()); // Git
            // Bob Lead has System Design level 5, Java level 4
            userSkillRepository.save(UserSkill.builder().user(lead).skill(skills.get(6)).level(5).build()); // System Design
            userSkillRepository.save(UserSkill.builder().user(lead).skill(skills.get(0)).level(4).build()); // Java
            // Eve QA has Testing level 5
            userSkillRepository.save(UserSkill.builder().user(qa).skill(skills.get(7)).level(5).build()); // Testing

            // 5. Seed Project
            Project project = projectRepository.save(Project.builder()
                    .name("Jira Clone Project")
                    .description("Graduation Project - Build a collaborative project management system modeled on Jira.")
                    .ownerId(pm.getId())
                    .status("ACTIVE")
                    .startDate(LocalDate.now().minusDays(14))
                    .endDate(LocalDate.now().plusMonths(6))
                    .build());

            // 6. Seed Project Members and Project Member Roles
            ProjectMember pmMember = projectMemberRepository.save(ProjectMember.builder().project(project).user(pm).build());
            projectMemberRoleRepository.save(ProjectMemberRole.builder().projectMember(pmMember).projectRole(ownerProjRole).build());

            ProjectMember leadMember = projectMemberRepository.save(ProjectMember.builder().project(project).user(lead).build());
            projectMemberRoleRepository.save(ProjectMemberRole.builder().projectMember(leadMember).projectRole(leadProjRole).build());

            ProjectMember dev1Member = projectMemberRepository.save(ProjectMember.builder().project(project).user(dev1).build());
            projectMemberRoleRepository.save(ProjectMemberRole.builder().projectMember(dev1Member).projectRole(devProjRole).build());

            ProjectMember dev2Member = projectMemberRepository.save(ProjectMember.builder().project(project).user(dev2).build());
            projectMemberRoleRepository.save(ProjectMemberRole.builder().projectMember(dev2Member).projectRole(devProjRole).build());

            ProjectMember qaMember = projectMemberRepository.save(ProjectMember.builder().project(project).user(qa).build());
            projectMemberRoleRepository.save(ProjectMemberRole.builder().projectMember(qaMember).projectRole(qaProjRole).build());

            // 7. Seed Sprints
            Sprint sprint1 = sprintRepository.save(Sprint.builder()
                    .project(project)
                    .name("Sprint 1")
                    .goal("Setup architecture & user authentication")
                    .startDate(LocalDate.now().minusDays(14))
                    .endDate(LocalDate.now().minusDays(1))
                    .status("COMPLETED")
                    .build());

            Sprint sprint2 = sprintRepository.save(Sprint.builder()
                    .project(project)
                    .name("Sprint 2")
                    .goal("Implement Kanban board and Task APIs")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(13))
                    .status("ACTIVE")
                    .build());

            // 8. Seed Tasks
            Task task1 = taskRepository.save(Task.builder()
                    .project(project)
                    .sprint(sprint1)
                    .title("Implement Login API")
                    .description("Build JWT Authentication and signup flow.")
                    .priority("HIGH")
                    .storyPoint(5)
                    .status("DONE")
                    .createdBy(dev1)
                    .build());

            Task task2 = taskRepository.save(Task.builder()
                    .project(project)
                    .sprint(sprint1)
                    .title("Design Database Schema")
                    .description("Define tables and database relationships for project entities.")
                    .priority("MEDIUM")
                    .storyPoint(3)
                    .status("DONE")
                    .createdBy(lead)
                    .build());

            Task task3 = taskRepository.save(Task.builder()
                    .project(project)
                    .sprint(sprint2)
                    .title("Implement Kanban Board API")
                    .description("Create end-points for fetching board data and moving tasks between columns.")
                    .priority("CRITICAL")
                    .storyPoint(8)
                    .status("IN_PROGRESS")
                    .createdBy(lead)
                    .build());

            Task task4 = taskRepository.save(Task.builder()
                    .project(project)
                    .sprint(sprint2)
                    .title("Write Unit Tests for Auth")
                    .description("Improve test coverage for authentication security services.")
                    .priority("LOW")
                    .storyPoint(2)
                    .status("TODO")
                    .createdBy(dev1)
                    .build());

            Task task5 = taskRepository.save(Task.builder()
                    .project(project)
                    .sprint(null) // Backlog
                    .title("Bug: Avatar URL validation")
                    .description("Validation fails when updating profile with blank avatar URL.")
                    .priority("HIGH")
                    .storyPoint(1)
                    .status("BACKLOG")
                    .createdBy(pm)
                    .build());

            // 9. Assign Tasks
            taskAssignmentRepository.save(TaskAssignment.builder().task(task1).user(dev1).build());
            taskAssignmentRepository.save(TaskAssignment.builder().task(task2).user(lead).build());
            taskAssignmentRepository.save(TaskAssignment.builder().task(task3).user(dev1).build());
            taskAssignmentRepository.save(TaskAssignment.builder().task(task3).user(dev2).build());
            taskAssignmentRepository.save(TaskAssignment.builder().task(task4).user(qa).build());

            // 10. Seed Comments
            commentRepository.save(Comment.builder()
                    .task(task1)
                    .user(dev1)
                    .content("Authentication API completed. Ready for security review.")
                    .build());

            commentRepository.save(Comment.builder()
                    .task(task1)
                    .user(lead)
                    .content("Approved the PR. Excellent work Charlie!")
                    .build());

            commentRepository.save(Comment.builder()
                    .task(task3)
                    .user(dev2)
                    .content("I will handle the drag-and-drop support on the frontend. Charlie will focus on the backend APIs.")
                    .build());

            // 11. Seed Task History
            taskHistoryRepository.save(TaskHistory.builder()
                    .task(task1)
                    .user(dev1)
                    .fieldName("status")
                    .oldValue("TODO")
                    .newValue("IN_PROGRESS")
                    .build());

            taskHistoryRepository.save(TaskHistory.builder()
                    .task(task1)
                    .user(dev1)
                    .fieldName("status")
                    .oldValue("IN_PROGRESS")
                    .newValue("DONE")
                    .build());

            taskHistoryRepository.save(TaskHistory.builder()
                    .task(task3)
                    .user(dev2)
                    .fieldName("status")
                    .oldValue("TODO")
                    .newValue("IN_PROGRESS")
                    .build());

            System.out.println("Database seeding successfully completed!");
        } else {
            System.out.println("Database already seeded. Skipping initial data populate.");
        }
    }
}
