package com.dnd.gongmuin.chat_inquiry.service;

import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.chat_inquiry.dto.AcceptChatResponse;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryMapper;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryRequest;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.repository.ChatInquiryRepository;
import com.dnd.gongmuin.chat_inquiry.dto.RejectChatResponse;
import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.chatroom.dto.ChatRoomMapper;
import com.dnd.gongmuin.chatroom.exception.ChatErrorCode;
import com.dnd.gongmuin.chatroom.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.service.CreditHistoryService;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.notification.domain.NotificationType;
import com.dnd.gongmuin.notification.dto.NotificationEvent;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatInquiryService {

	private static final int CHAT_REWARD = 2000;
	private final ChatInquiryRepository chatInquiryRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final QuestionPostRepository questionPostRepository;
	private final CreditHistoryService creditHistoryService;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public CreateChatInquiryResponse createChatInquiry(CreateChatInquiryRequest request, Member inquirer) {
		QuestionPost questionPost = getQuestionPostById(request.questionPostId());
		Member answerer = getMemberById(request.answererId());

		ChatInquiry chatInquiry = chatInquiryRepository.save(
			ChatInquiryMapper.toChatInquiry(questionPost, inquirer, answerer, request.message())
		);

		eventPublisher.publishEvent(
			new NotificationEvent(NotificationType.CHAT_REQUEST, chatInquiry.getId(), inquirer.getId(), answerer)
		);
		creditHistoryService.saveChatCreditHistory(CreditType.CHAT_REQUEST, inquirer);

		return ChatInquiryMapper.toCreateChatInquiryResponse(chatInquiry);
	}

	@Transactional(readOnly = true)
	public PageResponse<ChatInquiryResponse> getChatInquiresByMember(Member member, Pageable pageable) {
		Slice<ChatInquiryResponse> responsePage = chatInquiryRepository.getChatInquiresByMember(
			member, pageable
		);
		return PageMapper.toPageResponse(responsePage);
	}

	@Transactional
	public AcceptChatResponse acceptChat(Long chatInquiryId, Member answerer) {
		ChatInquiry chatInquiry = getChatProposalById(chatInquiryId);
		validateIfAnswerer(answerer, chatInquiry);
		chatInquiry.updateStatusAccepted();
		creditHistoryService.saveChatCreditHistory(CreditType.CHAT_ACCEPT, answerer);

		ChatRoom chatRoom = chatRoomRepository.save(
			ChatRoomMapper.toChatRoom(chatInquiry.getQuestionPost(), chatInquiry.getInquirer(), answerer)
		);
		eventPublisher.publishEvent(
			new NotificationEvent(NotificationType.CHAT_ACCEPT, chatInquiry.getId(), answerer.getId(),
				chatInquiry.getInquirer())
		);

		return ChatInquiryMapper.toAcceptChatResponse(chatInquiry, chatRoom);
	}

	@Transactional
	public RejectChatResponse rejectChat(Long chatInquiryId, Member answerer) {
		ChatInquiry chatInquiry = getChatProposalById(chatInquiryId);

		validateIfAnswerer(answerer, chatInquiry);
		chatInquiry.updateStatusRejected();
		creditHistoryService.saveChatCreditHistory(CreditType.CHAT_REFUND, chatInquiry.getInquirer());
		eventPublisher.publishEvent(
			new NotificationEvent(NotificationType.CHAT_REJECT, chatInquiry.getId(), answerer.getId(),
				chatInquiry.getInquirer())
		);

		return ChatInquiryMapper.toRejectChatResponse(chatInquiry);
	}

	@Transactional
	public void rejectChatAuto() {
		List<Long> rejectedInquirerIds = chatInquiryRepository.getAutoRejectedInquirerIds();
		chatInquiryRepository.updateChatInquiryStatusRejected();
		memberRepository.refundInMemberIds(rejectedInquirerIds, CHAT_REWARD);
		creditHistoryService.saveCreditHistoryInMemberIds(rejectedInquirerIds, CreditType.CHAT_REFUND, CHAT_REWARD);
	}

	private ChatInquiry getChatProposalById(Long id) {
		return chatInquiryRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ChatErrorCode.NOT_FOUND_CHAT_ROOM));
	}

	private static void validateIfAnswerer(Member member, ChatInquiry chatInquiry) {
		if (!Objects.equals(member.getId(), chatInquiry.getAnswerer().getId())) {
			throw new ValidationException(ChatErrorCode.UNAUTHORIZED_REQUEST);
		}
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
