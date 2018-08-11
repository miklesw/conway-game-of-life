<template>
  <div id="app">
    <h1>Game of Life</h1>
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
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
