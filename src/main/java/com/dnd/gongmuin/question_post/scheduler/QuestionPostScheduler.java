package com.dnd.gongmuin.question_post.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.question_post.service.QuestionPostService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QuestionPostScheduler {

	private final QuestionPostService questionPostService;

	@Transactional
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void closeQuestionPost() {
		questionPostService.changeQuestionPostStatusAnswerClosed(LocalDateTime.now());
	}
}
