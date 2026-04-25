package com.jobly.util;

import com.jobly.gen.model.SkillProficiency;
import com.jobly.model.JobSkillEntity;
import com.jobly.model.UserSkillEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkillSimilarityUtils {

    private static final float SKILL_MATCH_THRESHOLD = 0.4F;

    private static final float PROFICIENCY_REWARD_OR_PENALTY = -0.1F;

    private static final float BASE_PROFICIENCY_MATCH_SCORE = 1.0F;

    public static Float computeSkillMatch(List<JobSkillEntity> jobSkills, List<UserSkillEntity> userSkills) {
        if (userSkills.isEmpty()) return null;
        if (jobSkills.isEmpty()) return 0.0F;

        float totalScore = 0.0F;

        for (JobSkillEntity jobSkill : jobSkills) {
            Float[] jobVec = jobSkill.getSkill().getEmbedding();
            SkillProficiency expectedProficiency = jobSkill.getExpectedProficiency();

            float bestSimilarity = 0.0F;
            SkillProficiency bestUserProficiency = null;

            for (UserSkillEntity userSkill : userSkills) {
                Float[] userVec = userSkill.getSkill().getEmbedding();
                float similarity = SkillSimilarityUtils.cosineSimilarity(jobVec, userVec);
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestUserProficiency = userSkill.getProficiencyLevel();
                }
            }
            if (bestSimilarity < SKILL_MATCH_THRESHOLD) {
                continue;
            }

            float penaltyFactor = SkillSimilarityUtils.proficiencyPenaltyFactor(expectedProficiency, bestUserProficiency);
            totalScore += bestSimilarity * penaltyFactor;
        }

        float result = totalScore / jobSkills.size();
        return Math.clamp(result, 0.0F, 1.0F);
    }

    private static float proficiencyPenaltyFactor(SkillProficiency expectedProficiency, SkillProficiency actualProficiency) {
        if (expectedProficiency == null || actualProficiency == null) return BASE_PROFICIENCY_MATCH_SCORE;

        int gap = expectedProficiency.ordinal() - actualProficiency.ordinal();
        return BASE_PROFICIENCY_MATCH_SCORE + gap * PROFICIENCY_REWARD_OR_PENALTY;
    }

    private static float cosineSimilarity(Float[] a, Float[] b) {
        if (a == null || b == null || ObjectUtils.isEmpty(a) || ObjectUtils.isEmpty(b)) return 0.0F;

        float dot = 0F;
        float normA = 0F;
        float normB = 0F;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) return 0.0F;
        return (float) (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }
}
