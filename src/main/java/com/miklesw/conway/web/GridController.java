package com.miklesw.conway.web;

import com.miklesw.conway.grid.GridService;
import com.miklesw.conway.grid.model.CellPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
public class GridController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridController.class);

    private final GridService gridService;

    @Autowired
    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @RequestMapping(path="/spawn", method = RequestMethod.POST)
    public void spawnCell(@RequestBody CellPosition position ) {
        // TODO: random color for user.
        gridService.spawnCell(position, Color.RED);
    }



}
