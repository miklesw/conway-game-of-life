<template>
  <div id="app">
    <Grid/>
  </div>
</template>

<script>
  import Grid from './components/Grid'

  export default {
    name: 'App',
    components: {
      Grid
    }
  }
</script>

<style>
  html, body {
    height: 100%;
    margin: 0;
    padding: 0;
  }
  #app {
    text-align: center;
  }
</style>
