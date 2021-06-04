package com.scaffold.chat.service;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.scaffold.chat.domains.Email;

@Service
public class EmailService {
	
	@Autowired JavaMailSender mailSender;
	@Autowired freemarker.template.Configuration freemarkerTemplate;
	@Autowired  private Environment environment;
	
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	public void sendHtmlMail(Email email) {
		try {
			String env[] = environment.getActiveProfiles();
			String toAddress = null;
			for (String currentEnvironment : env) {
				if (currentEnvironment.equals("development")) {
					toAddress = email.getToAddress();
				}
				if (currentEnvironment.equals("localDevelopment")) {
					toAddress = System.getenv("TO_EMAIL_ADDRESS");
				}
				if (currentEnvironment.equals("staging")) {
					toAddress = System.getenv("TO_EMAIL_ADDRESS");
				}
			}
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(email.getTemplate(), email.getContext());
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
			helper.setFrom("${MAIL_USERNAME}");
			helper.setSubject(email.getSubject());
			System.out.println(toAddress);
			helper.setTo(toAddress);
			helper.setText(html, true);
			mailSender.send(message);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
