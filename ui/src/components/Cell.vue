<template>
  <td class="cell">
    <a v-if="!live" v-on:click="spawn">&nbsp;</a>
    <div v-if="live" v-bind:style="{backgroundColor: color}"></div>
  </td>
</template>

<script>
  import axios from 'axios';

  export default {
    name: 'Cell',
    props: ['live', 'color', 'cell-position'],
    methods: {
      spawn: function (event) {
        console.log("Attempt to spawn cell " + JSON.stringify(this.cellPosition));
        axios.post("/api/grid/cells/" + this.cellPosition.x + "/" + this.cellPosition.y  + "/spawn")
          .then(response => {
            // then nothing... we will receive cell state change event like everyone else
          })
          .catch(e => {
            console.log("Failed to spawn cell: " + e);
          });
      }
    }
  }
</script>

<style scoped>
  td {
    width: 20px;
    height: 20px;
    border: 1px solid black;
    resize: none;
    -webkit-touch-callout: none;
    -webkit-user-select: none;
    -khtml-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }

  td a {
    display: block;
    width: 100%;
    cursor: pointer;
  }

  td div {
    width: 100%;
    height: 100%;
  }
</style>
