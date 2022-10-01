package com.quartzdemo.payload;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EmailRequest {
	@Email
	@NotNull
	private String email;
	@NotNull
	private String subject;
	@NotNull
	private String body;
	@NotNull
	private LocalDateTime dateTime;
	@NotNull
	private ZoneId timeZone;
	
	
}
