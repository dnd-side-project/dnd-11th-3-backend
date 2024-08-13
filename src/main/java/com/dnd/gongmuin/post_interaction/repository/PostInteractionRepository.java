package com.dnd.gongmuin.post_interaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteraction;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {

	boolean existsByQuestionPostIdAndMemberIdAndType(
		Long questionPostId, Long memberId, InteractionType type
	);
	Optional<PostInteraction> findByQuestionPostIdAndMemberIdAndType(
		Long questionPostId, Long memberId, InteractionType type
	);
}
