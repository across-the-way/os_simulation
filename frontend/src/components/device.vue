
<template>
  <div id="app">
    <terminal name="my-terminal" @exec-cmd="onExecCmd" :show-header="false" ></terminal>
  </div>
</template>

<script>
  import Terminal from "vue-web-terminal"
  //  3.2.0 及 2.1.13 以后版本需要引入此样式，之前版本无需引入主题样式
  import 'vue-web-terminal/lib/theme/dark.css'

  export default {
    name: 'App',
    components: {Terminal},
    methods: {
      onExecCmd(key, command, success, failed) {
        if (key === 'fail') {
          failed('Something wrong!!!')
        } else {
          let allClass = ['success', 'error', 'system', 'info', 'warning'];

          let clazz = allClass[Math.floor(Math.random() * allClass.length)];
          success({
            type: 'normal',
            class: clazz,
            tag: '成功',
            content: command
          })
        }
      }
    }
  }
</script>

<style>
  body, html, #app {
    margin: 0;
    padding: 0;
    width: 90%;
    height: 90%;
  }
</style>