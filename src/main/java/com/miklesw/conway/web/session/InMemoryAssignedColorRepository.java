package com.miklesw.conway.web.session;

import com.google.common.collect.ImmutableSet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "session.color.repo.impl", havingValue = "memory", matchIfMissing = true)
public class InMemoryAssignedColorRepository implements AssignedColorRepository {

    private Set<Color> assignedColors = ConcurrentHashMap.newKeySet();

    @Override
    public void add(Color color) {
        assignedColors.add(color);
    }

    @Override
    public void remove(Color color) {
        assignedColors.remove(color);
    }

    @Override
    public Set<Color> getAll() {
        return ImmutableSet.copyOf(assignedColors);
    }
}
