package com.dnd.gongmuin.post_interaction.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.post_interaction.domain.Interaction;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InteractionJdbcRepository {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Transactional
	public void saveInteractions(List<Interaction> interactions) {
		jdbcTemplate.batchUpdate("insert into interaction "
				+ "(interaction_id, member_id, question_post_id, type, is_interacted) "
				+ "values (?,?,?,?,true)",
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Interaction interaction = interactions.get(i);
					ps.setLong(1, interaction.getId());
					ps.setLong(2, interaction.getMemberId());
					ps.setLong(3, interaction.getQuestionPostId());
					ps.setString(4, interaction.getType().name());
				}

				@Override
				public int getBatchSize() {
					return interactions.size();
				}
			});
	}
}
