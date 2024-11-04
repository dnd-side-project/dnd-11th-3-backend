package com.dnd.gongmuin.answer.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.member.domain.Member;

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
public class Answer extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "answer_id", nullable = false)
	private Long id;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "is_chosen", nullable = false)
	private Boolean isChosen;

	@Column(name = "is_questioner", nullable = false)
	private Boolean isQuestioner;

	private Long questionPostId;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	private Answer(String content, Boolean isQuestioner, Long questionPostId, Member member) {
		this.isChosen = false;
		this.content = content;
		this.isQuestioner = isQuestioner;
		this.questionPostId = questionPostId;
		this.member = member;
	}

	public static Answer of(String content, boolean isQuestioner, Long questionPostId, Member member) {
		return new Answer(content, isQuestioner, questionPostId, member);
	}

	public void updateIsChosen() {
		this.isChosen = true;
	}

	public void updateMember(Member anonymous) {
		this.member = anonymous;
	}
}
