package com.miklesw.conway.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    GridEventPublisherImpl springEventGridEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new GridEventPublisherImpl(applicationEventPublisher);
    }
}
