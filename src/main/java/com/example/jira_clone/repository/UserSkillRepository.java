package com.example.jira_clone.repository;

import com.example.jira_clone.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, String> {
    List<UserSkill> findByUserId(String userId);

    List<UserSkill> findBySkillId(Long skillId);
}
