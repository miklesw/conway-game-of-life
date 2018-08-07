# Conway's Game Of Life

A multi-player implementation of Conway’s Game of Life. 

The game is modeled as a grid with 4 simple rules:

- Any live cell with fewer than two live neighbours dies, as if caused by under-population.
- Any live cell with two or three live neighbours lives on to the next generation.
- Any live cell with more than three live neighbours dies, as if by overcrowding.
- Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

Each user will be assigned a random color when they attempt to spawn a cell (bring it to life), which will then be assigned to the cell. 

Any cells spawned out of reproduction will be assigned the average color of it's live nieghbouring cells.

This is an implementation of a recruitment technical challenge for [Fullstack-Backend Developers](https://hackmd.io/s/SyXikdg_g#Full-Stack--Backend-Developer--Eng-Manager).


## Deployment

## Testing

TODO explain why there is not unit test for GridService. 

## Design Decisions

### Packaging

Since this is a relatively small project all the packages have been implemented in a single module. 

The project is made up of 3 main packages which can be cleanly extracted to independent modules/projects:

- Grid (maintaining the grid's state)

- Events (implementation and configuration of grid events)
    - Grid
    
- Web (web layer implementation)
    - Events
    - Grid

TODO explain why grid and event packages don't use annotation for component scanning and autowiring, whereas web packagedoes

Furthermore, the grid and events packages could form part of an independent backend component if needed.

### Maintaining Grid State
The `GridService` makes use of a `Grid` bean to maintain the state of the grid. 

In this solution the `Grid` interface has been implemented as an `InMemoryGrid` consisting of:
 - `ConcurrentHashMap` to store `CellState` by `CellPosition` 
 - `ReentrantLock` to maintain grid locks (see Concurrency section below)

#### Scaling
To scale the application across multiple instances, the `Grid` interface can be implemented to use a distributed solution for storing states and locks, such as **Hazelcast**.

#### Concurrency

Although the `ConcurrentHashMap` is threadsafe, it does not ensure atomic operations (e.g read + write). Typically this is addressed using methods such as `computeIfAbsent()` or the `synchronized` keyword.

When computing the next grid state, the next state for the entire grid needs to be determined before any cell state changes are applied. This means that there is potential for a race condition while determining the next state; any user requests to spawn a cell may result in an inconsistent grid state or conflicts. (e.g. next state computation tries to spawn a cell with color X, but it has already been spawned by a user with color y).

The `synchonized` keyword does not provide sufficient flexibility to handle this scenario. `ReentrantLock` was used since it allows for a shared lock between the `spawnCell()`, `computeNextState()` and `killCell()` methods.

A problem with this approach is that `computeNextState()` may keep the lock for too long, depending on the size of the grid. This may result in wait timeouts if the front-end will expect a response when it makes a request to spawn a cell. Async requests for spawning cells may need to be considered.

### Cell Change Events

Cell change events are triggered when a user spawns a cell and for individual cell changes when the next grid state is applied. This allows individual cell updates to be sent to the frontend via websockets. As a result, the frontend only needs to receive the entire grid on initial connection or when reconnecting.

This implementation uses **Spring Events** for simplicity. The `@Async` annotation ensures that the handling of the event does not block the publishing thread.

#### Scaling
To scale the application across multiple instances, the application can be updated to use **Spring Integration** and a **Message Broker** instead of Spring Events, so that the `CellStateChangedEvent` is handled by the `CellStateChangeEventConsumer` in all instances.

NOTE: When using Spring Integration, the `GridEventPublisher` may need to be changed to accept `CellStateChangedEvent` (instead of 2 parameters), so that implementation-less gateways can be used.

### Maintaining a user's assigned color 

TODO: using session id to maintain a map of user colors