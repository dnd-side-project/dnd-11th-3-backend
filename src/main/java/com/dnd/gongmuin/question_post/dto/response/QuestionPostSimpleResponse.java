package com.dnd.gongmuin.question_post.dto.response;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class QuestionPostSimpleResponse {
	private Long questionPostId;
	private String title;
	private String content;
	private String jobGroup;
	private int reward;
	private String createdAt;
	@JsonProperty("isChosen")
	private boolean isChosen;
	@JsonProperty("isSaved")
	private boolean isSaved;
	@JsonProperty("isRecommended")
	private boolean isRecommended;
	private int savedCount;
	private int recommendCount;

	@QueryProjection
	public QuestionPostSimpleResponse(
		QuestionPost questionPost,
		int savedCount, int recommendCount) {
		this.questionPostId = questionPost.getId();
		this.title = questionPost.getTitle();
		this.content = questionPost.getContent();
		this.jobGroup = questionPost.getJobGroup().getLabel();
		this.reward = questionPost.getReward();
		this.createdAt = questionPost.getCreatedAt().toString();
		this.isChosen = questionPost.getIsChosen();
		this.savedCount = savedCount;
		this.recommendCount = recommendCount;
	}

	@Override
	public String toString() {
		return "QuestionPostSimpleResponse{" +
			"questionPostId=" + questionPostId +
			", title='" + title + '\'' +
			'}';
	}
}
