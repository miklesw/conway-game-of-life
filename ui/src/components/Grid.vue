<template>
  <div class="grid">
    <table>
      <Row v-for="row in rows" v-bind:cells="row"></Row>
    </table>
  </div>
</template>

<script>
  import axios from 'axios';
  import Row from './Row'

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
          let gridSize = response.data;
          console.log("Initializing grid of size: " + JSON.stringify(gridSize));
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
        })
        .catch(e => {
          console.log("Failed to initialize grid: " + e);
        });
    },
    created() {
      this.updateGridState();
    },
    methods: {
      updateGridState: function () {
        this._resetState();
        axios.get(`/api/grid/cells/live`)
          .then(response => {
            let liveCells = response.data;
            console.log("Updating grid state: " + JSON.stringify(liveCells));

            liveCells.forEach(function (liveCell) {
              let cells = this.rows[liveCell.position.y - 1];
              let cell = cells[liveCell.position.x - 1];
              cell.live = true;
              cell.color = liveCell.color;
            }, this);
          });
      },
      _resetState: function () {
        this.rows.forEach(function (row) {
          row.forEach(function (cell) {
            cell.live = false;
            cell.color = null;
          });
        });
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
    margin-left: auto;
    margin-right: auto;
  }
</style>
