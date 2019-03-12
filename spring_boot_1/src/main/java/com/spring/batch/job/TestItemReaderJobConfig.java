package com.spring.batch.job;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.batch.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class TestItemReaderJobConfig {

	@Autowired
	DataSourceConfig dataSource;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	private final static Integer chunkSize = 100;

	@Bean
	public Job testItemReaderJob() throws Exception {
		return jobBuilderFactory.get("testJob")
				.start(testItemReaderStep())
				.build();
	}

	@Bean
	public Step testItemReaderStep() throws Exception {
		return stepBuilderFactory.get("testStep")
				.<User, User>chunk(chunkSize)
				.reader(userItemReader())
				.writer(userItemWriter())
				.build();
	}

	@Bean
	public ItemReader<User> userItemReader() throws Exception{

		MyBatisCursorItemReader<User> reader = new MyBatisCursorItemReader<>();
		reader.setSqlSessionFactory(dataSource.getSqlSession());
		reader.setQueryId("getUser");

		return reader;
	}

	@Bean
	public ItemWriter<User> userItemWriter() throws Exception{

		MyBatisBatchItemWriter<User> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(dataSource.getSqlSession());
		writer.setStatementId("createUser");

		return writer;
	}
}
