package com.quartzdemo.web;
import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class EmailJob extends QuartzJobBean{
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailProperties mailProperties;
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		JobDataMap jobDataMap=jobExecutionContext.getMergedJobDataMap();
		
		String subject=jobDataMap.getString("subject");
		String body=jobDataMap.getString("body");
		String recipientEmail=jobDataMap.getString("email");
		
		sendMail(mailProperties.getUsername(),recipientEmail,subject,body);
	}
	
	private void sendMail(String fromEmail,String toEmail,String subject,String body) {
		try {
			MimeMessage message=mailSender.createMimeMessage();
			MimeMessageHelper messageHelper=new MimeMessageHelper(message,StandardCharsets.UTF_8.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(body);
			messageHelper.setFrom(fromEmail);
			messageHelper.setTo(toEmail);
			mailSender.send(message);
			
		}catch(MessagingException e){
			System.out.println(e);
			
		}
		
	}

}



