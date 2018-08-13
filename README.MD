# Conway's Game Of Life

A multi-player implementation of Conway’s Game of Life. 

The game is modeled as a grid with 4 simple rules:

- Any live cell with fewer than two live neighbours dies, as if caused by under-population.
- Any live cell with two or three live neighbours lives on to the next generation.
- Any live cell with more than three live neighbours dies, as if by overcrowding.
- Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

Users will be assigned a random color that will persist for the length of their session. If a color is assigned to a user session it may not be assigned to another user.

This is an implementation of a recruitment technical challenge for [Fullstack-Backend Developers](https://hackmd.io/s/SyXikdg_g#Full-Stack--Backend-Developer--Eng-Manager).


## Build & Deployment 

For ease of deployment and convenience, the UI has been packaged as a sub-module within the back-end app project. The [gradle-node-plugin](https://github.com/srs/gradle-node-plugin) is used to download **node & npm** and execute `npm run build`.

The `copyUIResources` gradle task copies generated production UI code into the app's `src/main/resources/public` folder.

This is far from ideal and would not be used in real life scenario. The UI should be an independent component deployed on a web server or reverse proxy such as **Nginx**, having mappings defined to direct requests to the backend app.

### Local Builds

Build: `./gradlew clean build`

Run: `./gradlew bootRun`

### Deploying on Heroku from Github

This application is configured to build (from Github) and deploy on Heroku without any additional configuration.

Git Url: https://github.com/miklesw/conway-game-of-life

NOTE: Gradle buildpack must be added to app on heroku.

## Configuration

| Property | Default | Desc |
|---|---|---|
| grid.size.x | n/a  | width of the grid in cells |
| grid.size.y | n/a  | height of the grid in cells |
| grid.impl | memory | implementation of the grid to use |
| session.color.repo.impl | memory | implementation of the session color repository to use |
| grid.next.state.interval.ms | n/a | interval in milliseconds to recompute grid state  |
| grid.next.state.enabled | n/a | enable recomputation of grid state (needs to be disabled for some tests) |


## Testing

Unit and web integration tests have been implemented as needed. Full service tests and additional coverage are desirable.

### Manual testing

The following features have been tested manually:

- Patterns:
    - Block
    - Glider
    - Beehive
    - Blinker 
- Color assignment for multiple browsers/user. 
- Application of average color when spawning cells 

## Backend Design 

### Packaging

Since this is a relatively small project all the packages have been implemented in a single module. 

The project is made up of 4 main packages which can be cleanly extracted to independent modules/projects:

- com.miklesw.conway.grid (maintaining the grid's state)
- com.miklesw.conway.events (implementation and configuration of grid events)
- com.miklesw.conway.schedule (scheduling of grid state computation)
- com.miklesw.conway.web (web layer implementation)


### Maintaining the grid's state
The `GridService` makes use of a `Grid` bean to maintain the state of the grid. 

In this solution the `Grid` interface has been implemented as an `InMemoryGrid` consisting of:
 - `ConcurrentHashMap` to store `CellState` by `CellPosition` 
 - `ReentrantLock` to maintain grid locks (see Concurrency section below)

NOTE: I was conflicted about whether the cell state and position belong together as part of a cell class, but in the end a map was the most sensible solution for accessing cell states, and I wasn't keen on having the position as both the map's key and an attribute.

#### Scaling
To scale the application across multiple instances, the `Grid` interface can be implemented to use a distributed solution for storing states and locks, such as **Hazelcast**.

#### Concurrency

Although the `ConcurrentHashMap` is threadsafe, it does not ensure atomic operations (e.g read + write). Typically this is addressed using methods such as `computeIfAbsent()` or the `synchronized` keyword.

When computing the next grid state, the next state for the entire grid needs to be determined before any cell state changes are applied. This means that there is potential for a race condition while determining the next state; any user requests to spawn a cell may result in an inconsistent grid state or conflicts. (e.g. next state computation tries to spawn a cell with color X, but it has already been spawned by a user with color y).

The `synchonized` keyword does not provide sufficient flexibility to handle this scenario. `ReentrantLock` was used since it allows for a shared lock between the `spawnCell()`, `computeNextState()` and `killCell()` methods.

A problem with this approach is that `computeNextState()` may keep the lock for too long, depending on the size of the grid. This may result in wait timeouts if the front-end will expect a response when it makes a request to spawn a cell. Async requests for spawning cells may need to be considered.

### Computing the next grid state

The grid state computation is triggers by a scheduled method that has a configurable fixed delay interval.

### Maintaining a user's assigned color 

There are many approaches available to maintain values for a user session with spring; `UserDetails` in the spring security context, session-scoped beans and session attributes.

For the purposes of this project, the most suitable was to store a session attribute in the http session object. 

An in-memory repository backed by a `Set` derived from `ConcurrentHashMap`, was implemented to maintain a list of assigned colors. This is used when generating a random color to ensure the color is not already assigned.

Leveraging Spring's **http session events**, the assigned color is added and removed to the repository when a session is created or destroyed (expired). This will ensure that the colors are recycled for future use.

#### Scaling

For color assignment to scale across multiple instances, 2 changes are required (unless sticky sessions are used):

- Implement a distributed repository for assigned colors.
- Implement distributed sessions (Spring's `SessionRepository`)

### Cell change events

Cell change events are triggered when a user spawns a cell and for individual cell changes when the next grid state is applied. This allows individual cell updates to be sent to the frontend via websockets. As a result, the frontend only needs to receive the entire grid on initial connection or when reconnecting.

This implementation uses **Spring Events** for simplicity. The `@Async` annotation ensures that the handling of the event does not block the publishing thread.

#### Scaling
To scale the application across multiple instances, the application can be updated to use **Spring Integration** and a **Message Broker** instead of Spring Events, so that the `CellStateChangedEvent` is handled by an event consumer in all instances.

NOTE: When using Spring Integration, the `GridEventPublisher` may need to be changed to accept `CellStateChangedEvent` (instead of 2 parameters), so that implementation-less gateways can be used.

### Publishing events to the client

A number of technologies have been considered to address this requirement, namely; **Websockets**, **Server-sent events (SSE)** and **polling**. 

A high-level comparison can be found [here](https://image.slidesharecdn.com/2015-06-22parisjswebsocketvsssev2-150624185438-lva1-app6891/95/websocket-vs-sse-parisjs-240615-11-638.jpg?cb=1435172291)

SSE was chosen based on 3 considerations:
- Unidirectional streams were sufficient given requests to spawn a cell were handled by a REST endpoint.
- Ease of implementation versus web sockets (polling was a non-starter)
- Multiple claims that SSE provides better support for load balancing and better latency.

Client-side support for Microsoft browsers can be addressed using one of many polyfils available.

NOTE: **Spring Webflux's** WebClient was used to build a test SSE client, but it didn't seem to be a natural fit for the event use-case on the server. Additional reading may be required to see how it can be applied.

### Endpoints

| Endpoint | Method | Desc |  
|---|---|---|
| /api/grid/size | GET |  Returns the size of the grid which is used by the front-end to generate the model and view | 
| /api/grid/cells/live | GET | Return a list of currently live cells, so that ui can re-initialise state on connect/reconnect |  
| /api/grid/cells/{x}/{y}/spawn | POST | Spawns a cell at a specified position |  
| /api/grid/cells/events | GET |  Subscribes to CellStateChangedEvents |  


## Frontend Design

### Web Framework

Angular, React and Vue were considered for this project. VueJs was selected because of the following reasons:

- Similarity with Angular (briefly worked with Angular in a previous role)
- Lightweight
- Recommended for small teams
- Quick learning curve

Axios was used for backend calls, since vue-resource is no longer recommended by Vue.

### Events 

Initially the event implementation was attempted using vue-sse, but it seems like this module is for an older version of Vue.

EventSource was used to handle SSE events. event-source-polyfill was installed to provide support for Microsoft browsers.

#### Re-connections 

When the EventSource connection is lost, some events maybe be lost and the state of the grid in the UI may become out of sync.

To prevent this from happening, the grid state is re-initialised every time the EventSource connection is opened (or re-opened) 

### Browser Compatibility

The UI was tested with Chrome 68 & Firefox 43.

ES6 syntax was used, so there might be issues in older browsers (there are ways around this)

### Responsive Design

The grid and cells should always maintain the same size. The page is set to overflow with scrolling if the grid is larger than the viewport.

### Possible Improvements

- Extract API calls to a service. More reading required to find out how to inject services like in Angular.
- Validation of vue component properties.
- Deterioration of responsiveness after having the page open for extended periods. 

## Unimplemented Features

The pattern toolbar hasn't been implemented due to time constraints.