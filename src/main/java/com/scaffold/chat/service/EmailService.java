package com.scaffold.chat.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.template.Template;

@Service
public class EmailService {
	
	@Autowired JavaMailSender mailSender;
	@Autowired freemarker.template.Configuration freemarkerTemplate;
	@Autowired  private Environment environment;
	
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	@Value("${spring.mail.username}")
	private String mailFrom;
	
	@Value("${com.scaffold.mail.override}")
	private String mailOverride;
	
	public void sendHtmlMail(String toAddress, String subject, Map<String, Object> context, String templateName) {
		try {
			String env[] = environment.getActiveProfiles();
			for (String currentEnvironment : env) {
				if (currentEnvironment.equals("development")) {
					toAddress = mailOverride;
				}
				if (currentEnvironment.equals("staging")) {
					toAddress = mailOverride;
				}
			}
			Template template = freemarkerTemplate.getTemplate(templateName);
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
			helper.setFrom(mailFrom);
			helper.setSubject(subject);
			helper.setTo(toAddress);
			helper.setText(html, true);
			mailSender.send(message);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
