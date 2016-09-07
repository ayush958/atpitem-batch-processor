package at.bkt.batch.atp.config;

import java.io.File;
import java.io.FilenameFilter;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataIntegrityViolationException;

import at.bkt.batch.atp.AtpArtikel;
import at.bkt.batch.atp.AtpArtikelDelimitedLineTokenizer;
import at.bkt.batch.atp.AtpArtikelItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);
	
	@Value("${import.file.path.source}")
	private String sourceFilePath;

	@Value("${import.file.path.done}")
	private String targetFilePath;
	
	
	@Value("${import.file.extension}")
	private String fileExtension;
	

	@Override
	@Autowired
	public void setDataSource(@Qualifier("batchDataSource") DataSource batchDataSource) {
		super.setDataSource(batchDataSource);
	}

	// tag::readerwriterprocessor[]
	@Bean
	public ItemReader<AtpArtikel> reader() {
		FlatFileItemReader<AtpArtikel> reader = new FlatFileItemReader<AtpArtikel>();
		reader.setResource(new FileSystemResource(getFileName()));
		reader.setLinesToSkip(1);
		reader.setLineMapper(new DefaultLineMapper<AtpArtikel>() {
			{

				
				setLineTokenizer(new AtpArtikelDelimitedLineTokenizer());
				setFieldSetMapper(new BeanWrapperFieldSetMapper<AtpArtikel>() {
					{
						setTargetType(AtpArtikel.class);
					}
				});
			}
		});

		reader.setEncoding("Windows-1252");
		return reader;
	}

	@Bean
	public ItemProcessor<AtpArtikel, AtpArtikel> processor() {
		return new AtpArtikelItemProcessor();
	}

	@Bean
	public ItemWriter<AtpArtikel> writer(@Qualifier("appDataSource") DataSource appDataSource) {
		JdbcBatchItemWriter<AtpArtikel> writer = new JdbcBatchItemWriter<AtpArtikel>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AtpArtikel>());
		writer.setSql("INSERT INTO ATPARTIKEL (ATPNR, BESCHR, ESPNR, EARTNR) VALUES (:atpnr, :beschreibung, :espnr, :eartnr)");
		writer.setDataSource(appDataSource);
		return writer;
	}
	// end::readerwriterprocessor[]


	
	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobBuilderFactory jobs, Step s1, JobExecutionListener listener) {
		return jobs.get("atpBatchImportJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(s1)
				.end()
				.build();
	}

	
	
	
	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<AtpArtikel> reader,
			ItemWriter<AtpArtikel> writer, ItemProcessor<AtpArtikel, AtpArtikel> processor) {
		return stepBuilderFactory.get("step1")
				.<AtpArtikel, AtpArtikel> chunk(10)
					.faultTolerant()
					.skip(DataIntegrityViolationException.class)
					.skipLimit(100000)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build(); 
	}
	// end::jobstep[]


	
	// private methods
	private String getFileName() {

		FilenameFilter filter = new  FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("." + getFileExtension());
			}
		};
		
		File file = new File(sourceFilePath);
		File[] files = file.listFiles(filter);
		
		String filename = files[0].getPath();		
		LOGGER.info("Process file {}.",filename);
		
		return filename;
	}

	
	public String getFileExtension() {
		return fileExtension;
	}
	
	public String getSourcePath() {
		return sourceFilePath;
	}
	
	public String getTargetPath() {
		return targetFilePath;
	}
	
	
	
}
