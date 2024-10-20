package com.dnd.gongmuin.common.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.common.config.QueryDslConfig;
import com.dnd.gongmuin.config.TestAuditingConfig;

import jakarta.persistence.EntityManager;

//repository용
@DataJpaTest(includeFilters = @ComponentScan.Filter(Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestAuditingConfig.class, QueryDslConfig.class})
public abstract class DataJpaTestSupport extends TestContainerSupport {
	@Autowired
	protected EntityManager em;
}
