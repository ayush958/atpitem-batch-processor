package at.bkt.batch.atp;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import at.bkt.batch.atp.config.BatchConfiguration;
import at.bkt.batch.atp.config.DataSourceConfiguration;

@Component
public class JobExecutionListener extends JobExecutionListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionListener.class);

	private final JdbcTemplate appJdbcTemplate;
	
	@Autowired
	BatchConfiguration batchConfiguration;
	
	@Autowired
	DataSourceConfiguration dataSourceConfiguration;

	@Autowired
	public JobExecutionListener(JdbcTemplate appJdbcTemplate) {
		this.appJdbcTemplate = appJdbcTemplate;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		dataSourceConfiguration.deleteTables();
		LOGGER.info("Table deletion done");
	
	}	
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			
			Date createTime = jobExecution.getCreateTime();
			Date endTime = jobExecution.getEndTime();
			
			moveFiles(batchConfiguration.getSourcePath(), batchConfiguration.getTargetPath());
			if (LOGGER.isInfoEnabled()) {
				int count = appJdbcTemplate.queryForObject("SELECT count(*) FROM ATPARTIKEL", Integer.class);
				LOGGER.info("!!! JOB FINISHED! Found <" + count + "> entities in the database.");
				LOGGER.info("Moved files to {}", batchConfiguration.getTargetPath());
				LOGGER.info("Time created: {}",createTime);
				LOGGER.info("Time finished: {}",endTime);
			}
		}
	}
	

	private void moveFile(final File f, final String newPath) {
		final File newLocation = new File(newPath + File.separator +  f.getName());
		newLocation.delete();
		f.renameTo(newLocation);
	}

	
	private void moveFiles(final String sourcePath, final String targetPath) {
		File sourceDir = new File(sourcePath);
		if(sourceDir.isDirectory()) {
		    File[] file = sourceDir.listFiles();
		    for(int i = 0; i < file.length; i++) {
		    	moveFile(file[i], targetPath);
		    }
		}
	}
	
}
