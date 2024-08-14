package com.dnd.gongmuin.post_interaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;

public interface InteractionCountRepository extends JpaRepository<InteractionCount, Long> {
	Optional<InteractionCount> findByQuestionPostIdAndType(
		Long questionPostId,
		InteractionType type
	);
}
