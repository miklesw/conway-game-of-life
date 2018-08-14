package com.miklesw.conway.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miklesw.conway.events.CellStateChangedEvent;
import com.miklesw.conway.web.model.CellStateChangeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CellChangeEventTestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CellChangeEventTestClient.class);

    private List<CellStateChangeInfo> receivedEvents = new ArrayList<>();

    private WebClient client;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Disposable eventSubscription;

    public CellChangeEventTestClient(String serverBaseUrl) {
        this.client = WebClient.create(serverBaseUrl);

    }

    public void listenToEvents() {
        receivedEvents.clear();
        eventSubscription = client.get().uri("/grid/cells/events")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .subscribe(this::handleEvent);

    }

    public void handleEvent(ServerSentEvent<String> event) {
        try {
            LOGGER.info("Handling event {} " + event);
            CellStateChangeInfo cellStateChangeInfo = objectMapper.readValue(event.data(), CellStateChangeInfo.class);
            receivedEvents.add(cellStateChangeInfo);
        } catch (IOException e) {
            LOGGER.warn("Failed to handle SSE payload for {} " + event);
        }
    }

    public List<CellStateChangeInfo> getReceivedEvents() {
        return receivedEvents;
    }

    public boolean receivedEvent(CellStateChangeInfo cellStateChangeInfo) {
        return receivedEvents.contains(cellStateChangeInfo);
    }

    public void close(){
        if (eventSubscription!= null && !eventSubscription.isDisposed()) {
            this.eventSubscription.dispose();
        }
    }
}
