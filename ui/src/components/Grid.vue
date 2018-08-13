<template>
  <div class="grid">
    <table>
      <Row v-for="row in rows" v-bind:cells="row"></Row>
    </table>
  </div>
</template>

<script>
  import axios from 'axios';
  import Row from './Row';

  let evtSource;

  export default {
    name: 'Grid',
    data() {
      return {
        rows: []
      }
    },
    beforeCreate() {
      axios.get(`/api/grid/size`)
        .then(response => {
          this._generateGrid(response.data);
          console.log("Initializing grid of size: " + JSON.stringify(gridSize));
        })
        .catch(e => {
          console.log("Failed to initialize grid: " + e);
        });
    },
    created() {
      this.updateGridState();
    },
    mounted() {
      evtSource = new EventSource("/api/grid/cells/events");
      let self = this;
      evtSource.addEventListener('cellStateChangedEvent', function (event) {
        console.log("Received cell event " + JSON.stringify(event));
        const data = JSON.parse(event.data);
        self._updateCellState(data.position, data.live, data.color);
      }, false);
    },
    beforeDestroy() {
      if (evtSource !== false) {
        evtSource.close();
      }
    },
    methods: {
      updateGridState: function () {
        this._resetState();
        axios.get(`/api/grid/cells/live`)
          .then(response => {
            let liveCells = response.data;
            console.log("Updating grid state: " + JSON.stringify(liveCells));

            liveCells.forEach(function (liveCell) {
              this._updateCellState(liveCell.position, true, liveCell.color);
            }, this);
          });
      },
      _updateCellState: function (position, live, color) {
        console.log("Updating cell " + JSON.stringify(position) + " with state " + live + " " + color);
        let cells = this.rows[position.y - 1];
        let cell = cells[position.x - 1];
        cell.live = live;
        cell.color = color;
      },
      _resetState: function () {
        this.rows.forEach(function (row) {
          row.forEach(function (cell) {
            cell.live = false;
            cell.color = null;
          });
        });
      },
      _generateGrid: function (gridSize) {
        for (let y = 1; y <= gridSize.y; y++) {
          let row = [];
          for (let x = 1; x <= gridSize.x; x++) {
            row.push({
              live: false,
              color: null,
              position: {
                x: x,
                y: y
              }
            });
          }
          this.rows.push(row);
        }
      }
    },
    components: {
      Row
    }
  }
</script>

<style scoped>
  table {
    border-collapse: collapse;
    table-layout: fixed;
    white-space: nowrap;
    width: fit-content;
    height: fit-content;
  }
  div.grid {
    margin: auto;
    overflow: scroll;
    width: 100%;
    height: 100vh;
  }
</style>
