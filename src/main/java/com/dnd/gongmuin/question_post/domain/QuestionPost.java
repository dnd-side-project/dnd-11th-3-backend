package com.dnd.gongmuin.question_post.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.List;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
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
	private final List<QuestionPostImage> images = new ArrayList<>();
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
		addImages(images);
	}

	private QuestionPost(Long id, String title, String content, int reward, JobGroup jobGroup,
		List<QuestionPostImage> images, Member member) {
		this.id = id;
		this.isChosen = false;
		this.title = title;
		this.content = content;
		this.reward = reward;
		this.jobGroup = jobGroup;
		this.member = member;
		addImages(images);
	}

	public static QuestionPost of(String title, String content, int reward, JobGroup jobGroup,
		List<QuestionPostImage> images, Member member) {
		return new QuestionPost(title, content, reward, jobGroup, images, member);
	}

	public static QuestionPost of(Long id, String title, String content, int reward, JobGroup jobGroup,
		List<QuestionPostImage> images, Member member) {
		return new QuestionPost(id, title, content, reward, jobGroup, images, member);
	}

	//==양방향 편의 메서드==//
	private void addImages(List<QuestionPostImage> images) {
		images.forEach(image -> {
			this.images.add(image);
			image.addQuestionPost(this);
		});
	}
}