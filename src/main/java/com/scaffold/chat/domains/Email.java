package com.scaffold.chat.domains;

import java.util.Map;

import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class Email {
	private String toAddress;
	private String subject;
	private Map<String, Object> context;
	private Template template;
}
