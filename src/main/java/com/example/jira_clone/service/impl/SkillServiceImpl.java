package com.example.jira_clone.service.impl;

import java.util.List;
import java.util.Optional;

import com.example.jira_clone.entity.Skill;
import com.example.jira_clone.repository.SkillRepository;
import com.example.jira_clone.service.SkillService;

public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Optional<List<Skill>> findAll() {
        return Optional.of(skillRepository.findAll());
    }

    @Override
    public Optional<Skill> findById(Long id) {
        return Optional.ofNullable(skillRepository.findById(id).orElse(null));
    }

    @Override
    public void deleteById(Long id) {
        skillRepository.deleteById(id);
    }

    @Override
    public Skill save(Skill skill) {
        return null;
    }

    @Override
    public Skill update(Skill skill) {
        return null;
    }
}
