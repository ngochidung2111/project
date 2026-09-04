package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.skill.SkillDto;
import com.example.jira_clone.dto.skill.SkillRequest;
import com.example.jira_clone.entity.Skill;
import com.example.jira_clone.repository.SkillRepository;
import com.example.jira_clone.service.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> findAll() {
        return skillRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SkillDto findById(Long id) {
        return skillRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found with id: " + id));
    }

    @Override
    @Transactional
    public SkillDto createSkill(SkillRequest request) {
        if (skillRepository.findByNameIgnoreCase(request.name()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill with name '" + request.name() + "' already exists");
        }
        Skill skill = Skill.builder().name(request.name()).build();
        Skill savedSkill = skillRepository.save(skill);
        return toDto(savedSkill);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SkillDto updateSkill(Long id, SkillRequest request) {
        Skill skillToUpdate = skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found with id: " + id));

        // Check if another skill with the new name already exists
        skillRepository.findByNameIgnoreCase(request.name()).ifPresent(existingSkill -> {
            if (!existingSkill.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill with name '" + request.name() + "' already exists");
            }
        });

        skillToUpdate.setName(request.name());
        Skill updatedSkill = skillRepository.save(skillToUpdate);
        return toDto(updatedSkill);
    }

    private SkillDto toDto(Skill skill) {
        return new SkillDto(skill.getId(), skill.getName());
    }
}
