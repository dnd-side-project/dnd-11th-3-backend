package com.dnd.gongmuin.post_interaction.domain;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;

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
public class InteractionCount extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "interaction_count_id", nullable = false)
	private Long id;

	@Column(name = "question_post_id")
	private Long questionPostId;

	@Column(name = "count", nullable = false)
	private int count;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private InteractionType type;

	private InteractionCount(InteractionType type, Long questionPostId) {
		this.count = 1;
		this.type = type;
		this.questionPostId = questionPostId;
	}

	public static InteractionCount of(InteractionType type, Long questionPostId) {
		return new InteractionCount(type, questionPostId);
	}

	public int increaseCount() {
		return ++count;
	}

	public int decreaseCount() {
		return --count;
	}
}
