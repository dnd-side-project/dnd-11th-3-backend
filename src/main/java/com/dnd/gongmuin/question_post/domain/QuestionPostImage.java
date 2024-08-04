package com.dnd.gongmuin.question_post.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionPostImage extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_post_image_id", nullable = false)
	private Long id;

	@Column(name = "image_url")
	private String imageUrl;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "question_post_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private QuestionPost questionPost;

	private QuestionPostImage(String imageUrl, QuestionPost questionPost) {
		this.imageUrl = imageUrl;
		this.questionPost = questionPost;
	}

	public static QuestionPostImage from(String imageUrl) {
		return new QuestionPostImage(imageUrl, null);
	}

	public void addQuestionPost(QuestionPost questionPost) {
		this.questionPost = questionPost;
	}
}
