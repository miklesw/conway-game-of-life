package com.miklesw.conway.web;

import com.miklesw.conway.grid.GridService;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import com.miklesw.conway.web.model.GridSize;
import com.miklesw.conway.web.model.LiveCell;
import com.miklesw.conway.web.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.awt.Color;
import java.util.List;
import java.util.Map;

import static com.miklesw.conway.utils.ColorUtils.toHexColor;
import static com.miklesw.conway.web.session.SessionAttributes.ASSIGNED_COLOR;
import static java.util.stream.Collectors.toList;

@RestController()
public class GridController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridController.class);

    private final GridService gridService;

    @Autowired
    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @RequestMapping(path = "/grid/size", method = RequestMethod.GET)
    public GridSize gridSize() {
        return null;
    }

    /**
     * The front-end only needs the current list of live cells to initialise grid state.
     * Dead cells can be determined using the grid size.
     */
    @RequestMapping(path = "/grid/cells/live", method = RequestMethod.GET)
    public List<LiveCell> liveCells() {
        Map<CellPosition, CellState> liveCells = gridService.findLiveCells();
        return toLiveCellList(liveCells);
    }

     /**
     * Not convinced that having a resourceId made up of 2 path variables is a valid REST path,
     * but I personally prefer this approach over having a string with delimiter.
     */
    @RequestMapping(path = "/grid/cells/{x}/{y}/spawn", method = RequestMethod.POST)
    public void spawnCell(@PathVariable int x, @PathVariable int y, HttpSession session) {
        gridService.spawnCell(new CellPosition(x, y), assignedColor(session));
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStateException.class})
    public String badRequest(Exception ex) {
        return ex.getMessage();
    }

    private Color assignedColor(HttpSession session) {
        return (Color)session.getAttribute(ASSIGNED_COLOR);
    }

    private List<LiveCell> toLiveCellList(Map<CellPosition, CellState> liveCells) {
        return liveCells.entrySet().stream()
                .map(e -> toLiveCell(e.getKey(), e.getValue()))
                .collect(toList());
    }

    private LiveCell toLiveCell(CellPosition p, CellState s) {
        return new LiveCell(new Position(p.getX(), p.getY()), toHexColor(s.getColor()));
    }
}
