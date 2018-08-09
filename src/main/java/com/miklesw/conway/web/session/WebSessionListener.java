package com.miklesw.conway.web.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.awt.*;

import static com.miklesw.conway.web.session.SessionAttributes.ASSIGNED_COLOR;

@Component
public class WebSessionListener {

    private final AssignedColorRepository assignedColorRepository;

    private final UnassignedColorFinder unassignedColorFinder;

    @Autowired
    public WebSessionListener(AssignedColorRepository assignedColorRepository, UnassignedColorFinder unassignedColorFinder) {
        this.assignedColorRepository = assignedColorRepository;
        this.unassignedColorFinder = unassignedColorFinder;
    }

    @EventListener
    public void onSessionCreationEvent(SessionCreationEvent SessionCreationEvent) {
        Color unassignedColor = unassignedColorFinder.find();
        ((HttpSessionCreatedEvent)SessionCreationEvent).getSession().setAttribute(ASSIGNED_COLOR, unassignedColor);
        assignedColorRepository.add(unassignedColor);
    }

    @EventListener
    public void sessionDestroyed(SessionDestroyedEvent sessionDestroyedEvent) {
        Color sessionAssignedColor = (Color) ((HttpSessionDestroyedEvent)sessionDestroyedEvent).getSession().getAttribute(ASSIGNED_COLOR);
        assignedColorRepository.remove(sessionAssignedColor);
    }

}