package com.dnd.gongmuin.save.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

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
public class SavedQuestion extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "saved_question_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "question_post_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private QuestionPost questionPost;
}