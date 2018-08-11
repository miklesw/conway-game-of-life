package com.miklesw.conway.web.events;

import com.miklesw.conway.events.CellStateChangedEvent;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import com.miklesw.conway.web.model.CellStateChangeInfo;
import com.miklesw.conway.web.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.miklesw.conway.utils.ColorUtils.toHexColor;


/**
 * Source based on https://golb.hplar.ch/2017/03/Server-Sent-Events-with-Spring.html
 */
@Controller
@RequestMapping("/api/grid")
public class CellStateChangedEventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CellStateChangedEventController.class);

    // TODO: this should be within a bean
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @RequestMapping(value = "/cells/events", method = RequestMethod.GET)
    public SseEmitter handle() {

        SseEmitter emitter = new SseEmitter();
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));

        return emitter;
    }



    @Async
    @EventListener
    public void onCellStateChanged(CellStateChangedEvent event) {
        LOGGER.info("Handling cell state change event for cell at {} with {}", event.getCellPosition(), event.getCellState());

        // TODO: Event listener shouldn't be in the controller.

        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach(emitter -> {
            try {
                CellStateChangeInfo cellStateChangeInfo = toCellStateChangeInfo(event);
                emitter.send(cellStateChangeInfo);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });

        this.emitters.removeAll(deadEmitters);
    }

    private CellStateChangeInfo toCellStateChangeInfo(CellStateChangedEvent event) {
        CellPosition pos = event.getCellPosition();
        CellState state = event.getCellState();
        Position position = new Position(pos.getX(), pos.getY());
        return new CellStateChangeInfo(position, state.isLive(), toHexColor(state.getColor()));
    }

}
