package com.miklesw.conway.web.session;

import java.awt.*;
import java.util.Set;

public interface AssignedColorRepository {

    void add(Color color);

    void remove(Color color);

    Set<Color> getAll();

}
