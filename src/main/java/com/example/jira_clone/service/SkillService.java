package com.example.jira_clone.service;

import java.util.List;
import java.util.Optional;

import com.example.jira_clone.entity.Skill;

public interface SkillService {
    Optional<List<Skill>> findAll();
    Optional<Skill> findById(Long id);
    Skill save(Skill skill);
    void deleteById(Long id);
    Skill update(Skill skill);
}