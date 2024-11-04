package com.dnd.gongmuin.question_post.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.exception.AnswerErrorCode;
import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionPost extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_post_id", nullable = false)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "reward", nullable = false)
	private int reward;

	@Enumerated(EnumType.STRING)
	@Column(name = "job_group", nullable = false)
	private JobGroup jobGroup;

	@Column(name = "is_chosen", nullable = false)
	private Boolean isChosen;

	@OneToMany(mappedBy = "questionPost", cascade = CascadeType.ALL)
	private List<QuestionPostImage> images = new ArrayList<>();

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	private QuestionPost(String title, String content, int reward, JobGroup jobGroup,
		List<QuestionPostImage> images, Member member) {
		this.isChosen = false;
		this.title = title;
		this.content = content;
		this.reward = reward;
		this.jobGroup = jobGroup;
		this.member = member;
		initPostImages(images);
	}

	public static QuestionPost of(String title, String content, int reward, JobGroup jobGroup,
		List<QuestionPostImage> images, Member member) {
		return new QuestionPost(title, content, reward, jobGroup, images, member);
	}

	//==양방향 편의 메서드==//
	private void initPostImages(List<QuestionPostImage> images) {
		images.forEach(image -> {
			this.images.add(image);
			image.addQuestionPost(this);
		});
	}

	public void updatePostImages(List<String> imageUrls) {
		List<QuestionPostImage> questionPostImages = new ArrayList<>();
		imageUrls.stream().map(QuestionPostImage::from)
			.forEach(questionPostImage -> {
				questionPostImage.addQuestionPost(this);
				questionPostImages.add(questionPostImage);
			});
		this.images = questionPostImages;
	}

	public void clearPostImages() {
		this.images.clear();
	}

	public boolean isQuestioner(Long memberId) {
		return Objects.equals(this.member.getId(), memberId);
	}

	public void updateIsChosen(Answer answer) {
		if (Boolean.TRUE.equals(this.isChosen))
			throw new ValidationException(AnswerErrorCode.ALREADY_CHOSEN_ANSWER_EXISTS);
		this.isChosen = true;
		answer.updateIsChosen();
	}

	public void updateQuestionPost(
		String title, String content, int reward, JobGroup jobGroup
	) {
		this.title = title;
		this.content = content;
		this.reward = reward;
		this.jobGroup = jobGroup;
	}

	public void updateMember(Member anonymous) {
		this.member = anonymous;
	}
}