package com.dnd.gongmuin.chat_inquiry.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.chat_inquiry.dto.AcceptChatResponse;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryDetailResponse;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryMapper;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryRequest;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.ExpiredChatInquiryDto;
import com.dnd.gongmuin.chat_inquiry.dto.RejectChatResponse;
import com.dnd.gongmuin.chat_inquiry.exception.ChatInquiryErrorCode;
import com.dnd.gongmuin.chat_inquiry.repository.ChatInquiryRepository;
import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.chatroom.dto.ChatMessageMapper;
import com.dnd.gongmuin.chatroom.dto.ChatRoomMapper;
import com.dnd.gongmuin.chatroom.repository.ChatMessageRepository;
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
	private final ChatMessageRepository chatMessageRepository;
	private final AnswerRepository answerRepository;

	@Transactional
	public CreateChatInquiryResponse createChatInquiry(CreateChatInquiryRequest request, Member inquirer) {
		QuestionPost questionPost = getQuestionPostById(request.questionPostId());
		Member answerer = getMemberById(request.answererId());
		validateChatAnswerer(request.questionPostId(), inquirer.getId(), answerer);
		validateIfInquiryExists(inquirer, answerer, questionPost);
		ChatInquiry chatInquiry = chatInquiryRepository.save(
			ChatInquiryMapper.toChatInquiry(questionPost, inquirer, answerer, request.inquiryMessage())
		);

		saveInquirerCreditHistory(inquirer);

		eventPublisher.publishEvent(
			new NotificationEvent(NotificationType.CHAT_REQUEST, chatInquiry.getId(), inquirer.getId(), answerer)
		);

		return ChatInquiryMapper.toCreateChatInquiryResponse(chatInquiry);
	}

	private void validateIfInquiryExists(Member inquirer, Member answerer, QuestionPost questionPost) {
		if (chatInquiryRepository.existsByInquirerAndAnswererAndQuestionPost(inquirer, answerer, questionPost)) {
			throw new ValidationException(ChatInquiryErrorCode.ALREADY_REQUESTED);
		}
	}

	@Transactional(readOnly = true)
	public ChatInquiryDetailResponse getChatInquiryById(Long chatInquiryId, Member member) {
		ChatInquiry chatInquiry = getChatInquiryById(chatInquiryId);

		return ChatInquiryMapper.toChatInquiryDetailResponse(
			chatInquiry,
			member,
			chatInquiry.getChatPartner(member),
			chatInquiry.isInquirer(member)
		);
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
		ChatInquiry chatInquiry = getChatInquiryById(chatInquiryId);
		validateIfAnswerer(answerer, chatInquiry);
		chatInquiry.updateStatusAccepted();
		creditHistoryService.saveCreditHistory(CreditType.CHAT_ACCEPT, CHAT_REWARD, answerer);

		ChatRoom chatRoom = chatRoomRepository.save(
			ChatRoomMapper.toChatRoom(chatInquiry.getQuestionPost(), chatInquiry.getInquirer(), answerer)
		);
		chatMessageRepository.save(
			ChatMessageMapper.toChatMessage(chatInquiry.getMessage(), chatRoom)
		);
		eventPublisher.publishEvent(
			new NotificationEvent(NotificationType.CHAT_ACCEPT, chatInquiry.getId(), answerer.getId(),
				chatInquiry.getInquirer())
		);

		return ChatInquiryMapper.toAcceptChatResponse(chatInquiry, chatRoom);
	}

	@Transactional
	public RejectChatResponse rejectChat(Long chatInquiryId, Member answerer) {
		ChatInquiry chatInquiry = getChatInquiryById(chatInquiryId);

		validateIfAnswerer(answerer, chatInquiry);
		chatInquiry.updateStatusRejected();
		creditHistoryService.saveCreditHistory(CreditType.CHAT_REFUND, CHAT_REWARD, chatInquiry.getInquirer());
		eventPublisher.publishEvent(
			new NotificationEvent(NotificationType.CHAT_REJECT, chatInquiry.getId(), answerer.getId(),
				chatInquiry.getInquirer())
		);

		return ChatInquiryMapper.toRejectChatResponse(chatInquiry);
	}

	@Transactional
	public void autoRejectChatInquiry(LocalDateTime now) {
		List<ExpiredChatInquiryDto> expiredChatInquiryDtos = chatInquiryRepository.getExpiredChatInquires();
		List<Long> expiredChatInquiryIds = getExpiredChatInquiryIds(expiredChatInquiryDtos);

		chatInquiryRepository.updateChatInquiryStatusRejected(expiredChatInquiryIds, now);
		refundAutoRejectedInquiry(expiredChatInquiryDtos);
		notifyAutoRejectedInquiry(expiredChatInquiryDtos);
	}

	private void validateChatAnswerer(Long questionPostId, Long inquirerId, Member answerer) {
		validateIfAnswererExists(questionPostId, answerer);
		validateIfNotSelfInquiry(inquirerId, answerer);
	}

	private void validateIfAnswererExists(Long questionPostId, Member answerer) {
		if (!answerRepository.existsByQuestionPostIdAndMember(questionPostId, answerer)) {
			throw new ValidationException(ChatInquiryErrorCode.NOT_EXISTS_ANSWERER);
		}
	}

	private void validateIfNotSelfInquiry(Long inquirerId, Member answerer) {
		if (Objects.equals(answerer.getId(), inquirerId)) {
			throw new ValidationException(ChatInquiryErrorCode.SELF_INQUIRY_NOT_ALLOWED);
		}
	}

	private void saveInquirerCreditHistory(Member inquirer) {
		memberRepository.save(inquirer);
		creditHistoryService.saveCreditHistory(CreditType.CHAT_REQUEST, CHAT_REWARD, inquirer);
	}

	private List<Long> getExpiredChatInquiryIds(List<ExpiredChatInquiryDto> expiredChatInquiryDtos) {
		return expiredChatInquiryDtos.stream()
			.map(ExpiredChatInquiryDto::chatInquiryId)
			.toList();
	}

	private List<Long> getRejectedInquirerIds(List<ExpiredChatInquiryDto> expiredChatInquiryDtos) {
		return expiredChatInquiryDtos.stream()
			.map(dto -> dto.inquirer().getId())
			.toList();
	}

	private void refundAutoRejectedInquiry(List<ExpiredChatInquiryDto> expiredChatInquiryDtos) {
		List<Long> rejectedInquirerIds = getRejectedInquirerIds(expiredChatInquiryDtos);
		memberRepository.refundInMemberIds(rejectedInquirerIds, CHAT_REWARD);
		creditHistoryService.saveCreditHistoryInMemberIds(
			rejectedInquirerIds, CreditType.CHAT_REFUND, CHAT_REWARD
		);
	}

	private void notifyAutoRejectedInquiry(List<ExpiredChatInquiryDto> expiredChatInquiryDtos) {
		for (ExpiredChatInquiryDto rejectChatInquiry : expiredChatInquiryDtos) {
			eventPublisher.publishEvent(    // 채팅 요청자 알림
				new NotificationEvent(
					NotificationType.AUTO_CHAT_REJECT,
					rejectChatInquiry.chatInquiryId(),
					rejectChatInquiry.inquirer().getId(),
					rejectChatInquiry.inquirer())
			);
			eventPublisher.publishEvent(
				new NotificationEvent(        // 채팅 답변자 알림
					NotificationType.AUTO_CHAT_REJECT,
					rejectChatInquiry.chatInquiryId(),
					rejectChatInquiry.answer().getId(),
					rejectChatInquiry.answer())
			);
		}
	}

	private ChatInquiry getChatInquiryById(Long id) {
		return chatInquiryRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ChatInquiryErrorCode.NOT_FOUND_INQUIRY));
	}

	private static void validateIfAnswerer(Member member, ChatInquiry chatInquiry) {
		if (!Objects.equals(member.getId(), chatInquiry.getAnswerer().getId())) {
			throw new ValidationException(ChatInquiryErrorCode.UNAUTHORIZED_REQUEST);
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
