package com.dnd.gongmuin.post_interaction.domain;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.post_interaction.exception.PostInteractionErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostInteraction extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_interaction_id", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private InteractionType type;

	@Column(name = "question_post_id")
	private Long questionPostId;

	@Column(name = "is_interacted", nullable = false)
	private Boolean isInteracted;

	@Column(name = "member_id")
	private Long memberId;

	private PostInteraction(InteractionType type, Long memberId, Long questionPostId) {
		this.isInteracted = true;
		this.type = type;
		this.memberId = memberId;
		this.questionPostId = questionPostId;
	}

	public static PostInteraction of(InteractionType type, Long memberId, Long questionPostId) {
		return new PostInteraction(type, memberId, questionPostId);
	}

	public void updateIsInteractedTrue(){
		if (Boolean.TRUE.equals(isInteracted)){
			throw new ValidationException(PostInteractionErrorCode.ALREADY_UNINTERACTED);
		}
		isInteracted = true;
	}

	public void updateIsInteractedFalse(){
		if (Boolean.FALSE.equals(isInteracted)){
			throw new ValidationException(PostInteractionErrorCode.ALREADY_INTERACTED);
		}
		isInteracted = false;
	}
}
