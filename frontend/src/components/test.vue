<template>
  <div>
    <el-button @click="handleButtonClick1">按钮1</el-button>
    <el-button @click="handleButtonClick2">按钮2</el-button>
  </div>
</template>

<script>
import axios from "axios"
import { serverURL } from "./ServerURL"
export default {
  methods: {
    handleButtonClick1() {
      // 处理按钮1点击时的逻辑
      console.log("msg")
      axios.get(serverURL + "/stop", {})
        .then(response => {
          console.log(response.data)
        }
        )
    },
    handleButtonClick2() {
      // 处理按钮2点击时的逻辑
      console.log("msg")
      axios.get(serverURL + "/start", {})
        .then(response => {
          console.log(response.data)
        }
        )
    }
  }
}
</script>