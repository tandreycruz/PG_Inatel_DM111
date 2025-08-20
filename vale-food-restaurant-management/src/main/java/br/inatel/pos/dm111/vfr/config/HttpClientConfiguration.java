package br.inatel.pos.dm111.vfr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfiguration
{
	@Bean
	public RestTemplate restTemplate()
	{
		return new RestTemplate();
	}
}
