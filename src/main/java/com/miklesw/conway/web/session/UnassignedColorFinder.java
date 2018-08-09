package com.miklesw.conway.web.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

import static com.miklesw.conway.utils.ColorUtils.randomColor;

@Component
public class UnassignedColorFinder {

    private final AssignedColorRepository assignedColorRepository;

    @Autowired
    public UnassignedColorFinder(AssignedColorRepository assignedColorRepository) {
        this.assignedColorRepository = assignedColorRepository;
    }

    public Color find() {
        Color unassignedColor = null;

        while (unassignedColor == null) {
            Color randomColor = randomColor();
            if (! assignedColorRepository.getAll().contains(randomColor)) {
                unassignedColor = randomColor;
            }
        }
        return unassignedColor;
    }

}
