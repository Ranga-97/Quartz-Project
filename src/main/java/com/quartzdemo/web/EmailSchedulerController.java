package com.quartzdemo.web;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.quartzdemo.payload.EmailRequest;
import com.quartzdemo.payload.EmailResponse;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class EmailSchedulerController {
	@Autowired
	private Scheduler scheduler;
	@PostMapping("/schedule/email")
	public ResponseEntity<EmailResponse>  scheduleEmail(@Valid @RequestBody EmailRequest emailRequest){
		try {
			ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
			if(dateTime.isBefore(ZonedDateTime.now())) {
				EmailResponse emailResponse = new EmailResponse(false,"dateTime must be after current time.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
			}
			JobDetail jobDetail =  buildJobDetail(emailRequest);
			Trigger trigger = BuildTrigger(jobDetail, dateTime);
			scheduler.scheduleJob(jobDetail,trigger);
			EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(),jobDetail.getKey().getGroup(),"EmailSchedule sent successfully");
			return ResponseEntity.ok(emailResponse);
			
		}catch (SchedulerException se) {
			log.error("Error while scheduling email:",se);
			EmailResponse emailResponse = new EmailResponse(false,"Error while scheduling email.please try again!");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
			
		}
	}
	
	@GetMapping("/get")
	public ResponseEntity<String> getApiTest(){
		return ResponseEntity.ok("Get Api test-sucess");
	}
	
	
	private JobDetail buildJobDetail(EmailRequest scheduledEmailRequest) {
		
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("email",scheduledEmailRequest.getEmail());
		
		jobDataMap.put("subject",scheduledEmailRequest.getSubject());
		jobDataMap.put("body", scheduledEmailRequest.getBody());
		return JobBuilder.newJob(EmailJob.class)
				.withIdentity(UUID.randomUUID().toString(),"email-jobs")
				.withDescription("send email job")
				.usingJobData(jobDataMap)
				.storeDurably()
				.build();
	}
	private Trigger BuildTrigger(JobDetail jobDetail,ZonedDateTime startAt) {
		return TriggerBuilder.newTrigger()
				.forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(),"email-Triggers")
				.withDescription("send email triggers")
				.startAt(Date.from(startAt.toInstant()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
				.build();
	}
	
}
