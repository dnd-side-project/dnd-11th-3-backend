package com.dnd.gongmuin.post_interaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteractionCount;

public interface PostInteractionCountRepository extends JpaRepository<PostInteractionCount, Long> {
	Optional<PostInteractionCount> findByQuestionPostIdAndType(
		Long questionPostId,
		InteractionType type
	);
}
