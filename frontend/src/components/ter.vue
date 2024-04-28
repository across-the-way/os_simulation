<template>

  <terminal name="my-terminal" @exec-cmd="onExecCmd" :show-header="false" :command-store="com" :context="content">
  </terminal>

</template>

<script>
import Terminal from "vue-web-terminal"
//  3.2.0 及 2.1.13 以后版本需要引入此样式，之前版本无需引入主题样式
import 'vue-web-terminal/lib/theme/dark.css'

import axios from "axios"
import { serverURL } from "../configjs/ServerURL"
export default {
  data() {
    return {
      com: [
        'cd',
        'git',
        'ls',
        'cat',
        'rf'
      ],
      content: '/root/data',
      responseData: [],
    }
  },
  created() {
    axios.post(serverURL + '/terminal', ['pwd'])
      .then(res => {
        this.content = res.data
        console.log(this.content)
      })
      .catch(err => {
        console.log(err)
      })
  },
  updated() {
    axios.post(serverURL + '/terminal', ['pwd'])
      .then(res => {
        this.content = res.data
      })
      .catch(err => {
        console.log(err)
      })
  },
  components: { Terminal },
  methods: {
    onExecCmd(key, command, success, failed) {
      let temp1 = []
      let temp = command.split(' ')
      temp.forEach(str => {
        const num = parseInt(str); // 转换为整数
        if (!isNaN(num)) {
          temp1.push(num); // 如果转换成功，则添加到args数组中
        }
        else
          temp1.push(str) // 否则就直接添加
      })
      console.log(temp1)
      axios.post(serverURL + '/terminal', temp1)
        .then(response => {
          console.log(response.data)
          this.responseData = response.data.split('\n')
          this.responseData.forEach(res => {
            success({
              type: 'normal',
              content: res
            })
          })


          console.log(this.responseData)
        })
        .catch(error => {
          console.log(error)
        })
      axios.post(serverURL + '/terminal', ['pwd'])
        .then(res => {
          this.content = res.data
        })
        .catch(err => {
          console.log(err)
        })
      console.log(this.responseData)
      if (key === 'fail') {
        failed('Something wrong!!!')
      } else {
        let allClass = ['success', 'error', 'system', 'info', 'warning'];

        let clazz = allClass[2];


        // failed({
        //   type: 'normal',
        //   class: 'success',
        //   tag: 'success',
        //   content: command
        // })
      }
    }
  }
}
</script>

<style>
body,
html,
#app {
  margin: 0;
  padding: 0;
  /* width: 90%; */
  height: 90%;
}
</style>