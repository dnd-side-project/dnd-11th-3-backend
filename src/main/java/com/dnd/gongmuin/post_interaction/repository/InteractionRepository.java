package com.dnd.gongmuin.post_interaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

	boolean existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(
		Long questionPostId, Long memberId, InteractionType type
	);

	Optional<Interaction> findByQuestionPostIdAndMemberIdAndType(
		Long questionPostId, Long memberId, InteractionType type
	);
}
