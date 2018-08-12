<template>
  <tr class="row">
    <Cell v-for="cell in cells"
          v-bind:live="cell.live"
          v-bind:color="cell.color"
          v-bind:cell-position="cell.position"></Cell>
  </tr>
</template>

<script>
  import Cell from './Cell'

  export default {
    name: 'Row',
    props: ['cells'],
    components: {
      Cell
    }
  }
</script>

<style scoped>
  td {
    padding: 0px;
  }
</style>
