package com.dnd.gongmuin.chat.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.dnd.gongmuin.chat.dto.response.LatestChatMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ChatMessageQueryRepository {

	private final MongoTemplate mongoTemplate;

	public List<LatestChatMessage> findLatestChatByChatRoomIds(List<Long> chatRoomIds) {
		AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "createdAt");
		AggregationOperation match = Aggregation.match(Criteria.where("chatRoomId").in(chatRoomIds));
		AggregationOperation group = Aggregation.group("chatRoomId")
			.first("createdAt").as("createdAt")
			.first("content").as("content")
			.first("type").as("type");
		AggregationOperation project = Aggregation.project()
			.and("_id").as("chatRoomId")
			.and("createdAt").as("createdAt")
			.and("content").as("content")
			.and("type").as("type");
		Aggregation aggregation = Aggregation.newAggregation(sort, match, group, project);
		AggregationResults<LatestChatMessage> results = mongoTemplate.aggregate(aggregation, "chat_messages",
			LatestChatMessage.class);
		return results.getMappedResults();
	}
}
