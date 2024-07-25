package com.dnd.gongmuin.post_interaction.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
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

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "question_post_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private QuestionPost questionPost;

	@Builder
	public PostInteractionCount(InteractionType type, QuestionPost questionPost) {
		this.type = type;
		this.questionPost = questionPost;
	}
}
