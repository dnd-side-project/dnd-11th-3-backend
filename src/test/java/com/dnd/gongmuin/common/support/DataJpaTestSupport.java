package com.dnd.gongmuin.common.support;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.dnd.gongmuin.config.TestAuditingConfig;

//repository용
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestAuditingConfig.class)
public abstract class DataJpaTestSupport extends TestContainerSupport {
}
