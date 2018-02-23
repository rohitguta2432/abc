package com.socioseer.restapp.service.email;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.socioseer.common.domain.EMAIL_TYPE;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class ThymeleafEmailTemplateResolver implements EmailTemplateResolver {

	private static final String CONTEXT = "context";
	private SpringTemplateEngine templateEngine;

	@Override
	public String resolve(EMAIL_TYPE event, Object content) {
		Context context = new Context();
		context.setVariable(CONTEXT, content);
		return templateEngine.process(event.toString(), context);
	}

}
