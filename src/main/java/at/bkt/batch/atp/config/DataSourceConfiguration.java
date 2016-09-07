package at.bkt.batch.atp.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DataSourceConfiguration {


	@Value("${db.delete.script}")
	private Resource deleteScript;

	@Value("${db.app.driverClassName}")
	private String appDriverClassName;

	@Value("${db.app.url}")
	private String appUrl;

	@Value("${db.app.username}")
	private String appUsername;

	@Value("${db.app.password}")
	private String appPassword;

	@Value("${db.batch.driverClassName}")
	private String batchDriverClassName;

	@Value("${db.batch.url}")
	private String batchUrl;

	@Value("${db.batch.username}")
	private String batchUsername;

	@Value("${db.batch.password}")
	private String batchPassword;

	@Bean
	public DataSource appDataSource()  {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource(appUrl, appUsername, appPassword);
		dataSource.setDriverClassName(appDriverClassName);
		DatabasePopulatorUtils.execute(deleteTablesPopulator(), dataSource);
		return dataSource;
	}

	@Bean
	public JdbcTemplate appJdbcTemplate(@Qualifier("appDataSource") final DataSource appDataSource) {
		return new JdbcTemplate(appDataSource);
	}

	@Bean
	@Primary
	public DataSource batchDataSource() throws SQLException {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource(batchUrl, batchUsername, batchPassword);
		dataSource.setDriverClassName(batchDriverClassName);
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(@Qualifier("batchDataSource") final DataSource batchDataSource) {
		return new JdbcTemplate(batchDataSource);
	}


	
	public void deleteTables() {
		DatabasePopulatorUtils.execute(deleteTablesPopulator(), appDataSource());
	}
	
	private DatabasePopulator deleteTablesPopulator() {
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(deleteScript);
		return populator;
	}

}
