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
- HashMap best choice for quick retrieval of cell state by position.
- Should CellPosition & CellState exist as part of a Cell object? 
- GridManager doing too much?  TODO: Review GridManager class. move grid locks and map to GridRepository?

#### Concurrency

Although the `ConcurrentHashMap` is threadsafe, it does not ensure atomic operations (e.g read + write). Typically this is addressed using `computeIfAbsent()` or the `synchronized` keyword.

When computing the next grid state, the next state for the entire grid needs to be determined before any cell state changes are applied. This means that there is race condition while determining the next state, any requests to spawn a cell may result in an inconsistent grid state or conflicts. (e.g. next state computation tries to spawn a cell with color X, but it has already been spawned by a user with color y).

The `synchonized` keyword does not provide sufficient flexibility to handle this scenario. `ReentrantLock` was used since it allows for a shared lock between the `spawnCell()`, `nextState()` and `killCell()` methods

A problem with this approach is that `nextState()` may keep the lock for too long depending on the size of the grid. This may result in wait timeouts if the front-end will expect a response when it makes a request to spawn a cell. Async requests for spawning cells may need to be considered.

### Cell Change Events

Cell change events are triggered when a user spawns a cell and for individual cell changes when the next grid state is applied. This allows individual cell updates to be sent to the frontend via websockets. As a result, the frontend only needs to receive the entire grid on initial connection or when reconnecting.

This implementation uses spring events for simplicity. The `GridEventPublisher` interface can be used to easily switch to other messaging technologies.


### Maintaining a user's assigned color 

TODO: using session id to maintain a map of user colors