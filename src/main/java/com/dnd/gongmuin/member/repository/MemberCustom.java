package com.dnd.gongmuin.member.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsByMemberResponse;
import com.dnd.gongmuin.member.dto.response.BookmarksByMemberResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsByMemberResponse;

public interface MemberCustom {
	Slice<QuestionPostsByMemberResponse> getQuestionPostsByMember(Member member, Pageable pageable);

	Slice<AnsweredQuestionPostsByMemberResponse> getAnsweredQuestionPostsByMember(Member member, Pageable pageable);

	Slice<BookmarksByMemberResponse> getBookmarksByMember(Member member, Pageable pageable);
}
