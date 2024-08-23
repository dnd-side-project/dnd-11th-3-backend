package com.dnd.gongmuin.member.domain;

import java.util.Arrays;

import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.exception.MemberErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobCategory {

	GME("일반기계"),
	AM("농업기계"),
	ELEC("전자"),
	TEXT("섬유"),
	GCE("일반화공"),

	ME("기계"),
	HT("난방"),
	TM("열관리"),
	OP("운전"),

	SM("경비관리"),

	BME("의공"),
	ENV("환경"),
	PH("보건"),
	SAN("위생"),
	PHARM("약무"),
	PATH("병리"),
	RAD("방사선"),
	RT("재활치료"),
	MR("의무기록"),

	NA("간호조무"),
	CK("조리"),

	IA("조사사무"),
	CM("법정경위"),
	CW("사무"),
	AS("행정사무"),
	CA("법원사무"),
	SG("속기"),

	VI("영상"),
	EO("전기 운영"),

	CO("교정"),
	AD("행정"),
	TX("세무"),
	ST("통계"),
	AU("감사"),
	EA("교육행정"),
	LB("사서"),
	CS("관세"),
	SW("사회복지");

	private final String label;

	public static JobCategory from(String input) {
		return Arrays.stream(values())
			.filter(category -> category.isEqual(input))
			.findAny()
			.orElseThrow(() -> new ValidationException(MemberErrorCode.NOT_FOUND_JOB_CATEGORY));
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
