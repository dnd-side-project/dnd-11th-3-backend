package com.dnd.gongmuin.member.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsResponse;
import com.dnd.gongmuin.member.dto.response.BookmarksResponse;
import com.dnd.gongmuin.member.dto.response.CreditHistoryResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsResponse;

public interface MemberCustom {
	Slice<QuestionPostsResponse> getQuestionPostsByMember(Member member, Pageable pageable);

	Slice<AnsweredQuestionPostsResponse> getAnsweredQuestionPostsByMember(Member member, Pageable pageable);

	Slice<BookmarksResponse> getBookmarksByMember(Member member, Pageable pageable);

	Slice<CreditHistoryResponse> getCreditHistoryByMember(String type, Member member, Pageable pageable);
}
