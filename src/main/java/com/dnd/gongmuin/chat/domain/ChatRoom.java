package com.dnd.gongmuin.chat.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
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
public class ChatRoom extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_room_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "question_post_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private QuestionPost questionPost;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "inquirer_id", nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member inquirer;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "answerer_id", nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member answerer;

	@Column(name = "is_accepted", nullable = false)
	private boolean isAccepted;

	private ChatRoom(QuestionPost questionPost, Member inquirer, Member answerer) {
		this.questionPost = questionPost;
		this.inquirer = inquirer;
		this.answerer = answerer;
		this.isAccepted = false;
		inquirer.decreaseCredit(2000);
	}

	public static ChatRoom of(
		QuestionPost questionPost,
		Member inquirer,
		Member answerer
	) {
		return new ChatRoom(questionPost, inquirer, answerer);
	}
}
