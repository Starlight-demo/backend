package starlight.backend.talent;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import starlight.backend.skill.model.entity.SkillEntity;
import starlight.backend.skill.model.response.SkillWithCategory;
import starlight.backend.talent.model.response.*;
import starlight.backend.talent.model.entity.PositionEntity;
import starlight.backend.talent.model.entity.TalentEntity;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface MapperTalent {
    default TalentProfile toTalentProfile(TalentEntity talent) {
        return TalentProfile.builder()
                .fullName(talent.getFullName())
                .id(talent.getTalentId())
                .position(talent.getPositions().stream()
                        .findAny()
                        .map(PositionEntity::getPosition)
                        .orElse(null))
                .avatar(talent.getAvatar())
                .build();
    }

    default TalentPagePagination toTalentPagePagination(Page<TalentEntity> talent) {
        return TalentPagePagination.builder()
                .data(talent.getContent().
                        stream().map(this::toTalentProfile).toList())
                .total(talent.getTotalElements())
                .build();
    }

    default TalentFullInfo toTalentFullInfo(TalentEntity talent) {
        return TalentFullInfo.builder()
                .fullName(talent.getFullName())
                .email(talent.getEmail())
                .birthday(talent.getBirthday())
                .avatar(talent.getAvatar())
                .experience(talent.getExperience())
                .education(talent.getEducation())
                .positions(talent.getPositions().stream().map(PositionEntity::getPosition).toList())
                .build();
    }

    default TalentPagePaginationWithFilterSkills toTalentListWithPaginationAndFilter(Page<TalentEntity> sortedTalent){
        return TalentPagePaginationWithFilterSkills.builder()
                .data(sortedTalent.getContent().stream()
                        .map(this::toTalentWithSkills)
                        .toList())
                .total(sortedTalent.getTotalElements())
                .build();
    }

    default TalentWithSkills toTalentWithSkills(TalentEntity talent) {
        return TalentWithSkills.builder()
                .id(talent.getTalentId())
                .fullName(talent.getFullName())
                .avatar(talent.getAvatar())
                .position(talent.getPositions().stream()
                        .findAny()
                        .map(PositionEntity::getPosition)
                        .orElse(null))
                .skill(talent.getTalentSkills()
                        .stream()
                        .map(this::toSkillWithCategory)
                        .toList())
                .build();
    }

    default SkillWithCategory toSkillWithCategory(SkillEntity skill) {
        return SkillWithCategory.builder()
                .skillId(skill.getSkillId())
                .category(skill.getCategory())
                .skill(skill.getSkill())
                .build();
    }
}

