package br.inatel.pos.dm111.vfr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.pubsub.v1.ProjectSubscriptionName;

@Configuration
public class PubSubConfiguration
{
	@Value("${vale-food.gae.project-id}")
	private String projectId;
	
	@Value("${vale-food.gae.users.subscription-name}")
	private String usersSubscriptionName;
	
	@Bean
	public ProjectSubscriptionName subscriptionName()
	{
		return ProjectSubscriptionName.of(projectId, usersSubscriptionName);
	}
	
	@Bean
	public ObjectMapper objectMapper()
	{
		return new ObjectMapper();
	}
}
