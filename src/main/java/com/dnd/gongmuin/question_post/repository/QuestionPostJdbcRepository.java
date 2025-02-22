package com.dnd.gongmuin.question_post.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionPostJdbcRepository {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Transactional
	public void saveQuestionPosts(List<QuestionPost> questionPosts) {
		jdbcTemplate.batchUpdate("insert into question_post "
				+ "(question_post_id, content, is_chosen, job_group, reward, title, member_id, status,created_at) "
				+ "values (?,?,?,?,?,?,?,?,?)",
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					QuestionPost questionPost = questionPosts.get(i);
					ps.setLong(1, questionPost.getId());
					ps.setString(2, questionPost.getContent());
					ps.setBoolean(3, questionPost.getIsChosen());
					ps.setString(4, questionPost.getJobGroup().name());
					ps.setInt(5, questionPost.getReward());
					ps.setString(6, questionPost.getTitle());
					ps.setLong(7, questionPost.getMember().getId());
					ps.setString(8, questionPost.getStatus().name());
					ps.setTimestamp(9, Timestamp.valueOf(questionPost.getCreatedAt()));
				}

				@Override
				public int getBatchSize() {
					return questionPosts.size();
				}
			});
	}
}
