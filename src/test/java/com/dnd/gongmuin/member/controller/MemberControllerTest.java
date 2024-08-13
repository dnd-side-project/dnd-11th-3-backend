package com.dnd.gongmuin.member.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.dto.request.UpdateMemberProfileRequest;

@DisplayName("[MemberController] 통합테스트")
class MemberControllerTest extends ApiTestSupport {

	@DisplayName("로그인 된 사용자 프로필 정보를 조회한다.")
	@Test
	void getMemberProfile() throws Exception {
		// when  // then
		mockMvc.perform(get("/api/members/profile")
				.header(AUTHORIZATION, accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("nickname").value("김회원"))
			.andExpect(jsonPath("jobGroup").value("공업"))
			.andExpect(jsonPath("jobCategory").value("가스"))
			.andExpect(jsonPath("credit").value(10000));
	}

	@DisplayName("로그인 된 사용자 프로필 정보를 수정한다.")
	@Test
	void updateMemberProfile() throws Exception {
		// given
		UpdateMemberProfileRequest request = new UpdateMemberProfileRequest("박회원", "행정", "가스");

		// when  // then
		mockMvc.perform(patch("/api/members/profile/edit")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("nickname").value("박회원"))
			.andExpect(jsonPath("jobGroup").value("행정"))
			.andExpect(jsonPath("jobCategory").value("가스"))
			.andExpect(jsonPath("credit").value(10000));
	}
}
