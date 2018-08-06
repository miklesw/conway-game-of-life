# Conway's Game Of Life

A multi-player implementation of Conway’s Game of Life. 

The game is modeled as a grid with 4 simple rules:

- Any live cell with fewer than two live neighbours dies, as if caused by under-population.
- Any live cell with two or three live neighbours lives on to the next generation.
- Any live cell with more than three live neighbours dies, as if by overcrowding.
- Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

Each user will be assigned a random color when they attempt to bring a cell to live, which will then be assigned to the cell. 

Any cells spawned out of reproduction will be assigned the average color of it's live nieghbouring cells.

## Deployment

## Testing

## Design Decisions

### Maintaining Grid State
- HashMap best choice for quick retrieval of cells by position.
- Should CellPosition & CellState exist as part of a Cell object? 
- GridManager doing too much?

TODO: review

### Concurrency

ConcurrentHashMap is not enough. What if a user attempts to spawn a cell while determining next state?

If the user spawns a cell that will be spawned in the next state, before the state has been applied but after it's next state has been determined, the GridManager will fail to apply the state to that cell.

TODO: handle exception. 
TODO: prevent conflict?

### Cell Change Events

Cell change events are triggered when a user spawns a cell and when the next grid state is applied. This allows individual cell updates to be sent to the frontend via websockets. As a result, the frontend only needs to receive the entire grid on initial connection or when reconnecting.

This implementation uses spring events for simplicity. The GridEventPublisher interface can be used to easily switch to other messaging technologies.


### Maintaining a user's assigned color 

TODO: using session id to maintain a map of user colors, so that 