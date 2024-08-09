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
public class PostInteractionCount extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_interaction_count_id", nullable = false)
	private Long id;

	@Column(name = "total_count", nullable = false)
	private int totalCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private InteractionType type;

	@Column(name = "question_post_id")
	private Long questionPostId;

	private PostInteractionCount(InteractionType type, Long questionPostId) {
		this.type = type;
		this.questionPostId = questionPostId;
	}

	public static PostInteractionCount of(InteractionType type, Long questionPostId) {
		return new PostInteractionCount(type, questionPostId);
	}

	private void increaseTotalCount(){
		totalCount++;
	}
}
