package com.dnd.gongmuin.post_interaction.domain;

import java.util.Objects;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.post_interaction.exception.InteractionErrorCode;

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
public class Interaction extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "interaction_id", nullable = false)
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

	private Interaction(InteractionType type, Long memberId, Long questionPostId) {
		this.isInteracted = true;
		this.type = type;
		this.memberId = memberId;
		this.questionPostId = questionPostId;
	}

	public static Interaction of(InteractionType type, Long memberId, Long questionPostId) {
		return new Interaction(type, memberId, questionPostId);
	}

	public void updateIsInteracted(boolean updateStatus) {
		if (Objects.equals(isInteracted, updateStatus)) {
			throw new ValidationException(InteractionErrorCode.ALREADY_INTERACTED);
		}
		isInteracted = updateStatus;
	}
}
