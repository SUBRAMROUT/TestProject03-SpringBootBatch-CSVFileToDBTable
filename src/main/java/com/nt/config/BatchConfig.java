package com.nt.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.nt.entity.Employee;
import com.nt.processor.EmployeeItemProcessor;
import com.nt.repository.IEmployeeRepository;

@Configuration
public class BatchConfig {
	@Autowired
	private EmployeeItemProcessor processor;
	@Autowired
	private IEmployeeRepository empRepo;
	
	@Bean
	public FlatFileItemReader<Employee> createReader(){
		return new FlatFileItemReaderBuilder<Employee>()
				.name("file-reader")
				.resource(new ClassPathResource("EmployeeInfo.csv"))
				.delimited()
				.names("empno","ename","eaddrs","salary")
				.targetType(Employee.class)
				.build();
	}
	
	@Bean
	public RepositoryItemWriter<Employee> createWriter(){
		return new RepositoryItemWriterBuilder<Employee>()
				.repository(empRepo)
				.methodName("save")
				.build();
	}
	
	@Bean(name="step1")
	public Step createStep1(JobRepository repository, PlatformTransactionManager txMgmr) {
		return new StepBuilder("step1",repository)
				.<Employee,Employee>chunk(3, txMgmr)
				.reader(createReader())
				.processor(processor)
				.writer(createWriter())
				.build();
	}
	
	@Bean(name="job1")
	public Job createJob1(JobRepository repository,Step step1) {
		return new JobBuilder("job1",repository)
				.incrementer(new RunIdIncrementer())
				.start(step1)
				.build();
	}

}
