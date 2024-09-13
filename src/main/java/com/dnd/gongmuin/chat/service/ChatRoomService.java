package com.dnd.gongmuin.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.dto.ChatMessageMapper;
import com.dnd.gongmuin.chat.dto.ChatRoomMapper;
import com.dnd.gongmuin.chat.dto.request.CreateChatRoomRequest;
import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.chat.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final QuestionPostRepository questionPostRepository;

	@Transactional(readOnly = true)
	public PageResponse<ChatMessageResponse> getChatMessages(Long chatRoomId, Pageable pageable) {
		Slice<ChatMessageResponse> responsePage = chatMessageRepository
			.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable)
			.map(ChatMessageMapper::toChatMessageResponse);
		return PageMapper.toPageResponse(responsePage);
	}

	@Transactional
	public ChatRoomDetailResponse createChatRoom(CreateChatRoomRequest request, Member inquirer) {
		QuestionPost questionPost = getQuestionPostById(request.questionPostId());
		Member answerer = getMemberById(request.answererId());
		return ChatRoomMapper.toChatRoomDetailResponse(
			chatRoomRepository.save(ChatRoomMapper.toChatRoom(questionPost, inquirer, answerer))
		);
	}

	private QuestionPost getQuestionPostById(Long id) {
		return questionPostRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
	}

	private Member getMemberById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));
	}
}

