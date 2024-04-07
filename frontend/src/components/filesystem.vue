<script setup>
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>

<script>
export default {
  data() {
    return {
      fileLocation: '',
    }
  },
  created() {
    axios.get(serverURL + window.location.pathname , {})
      .then(response => {
        // 处理响应结果
        console.log(response.data);
        this.responseData = response.data
      })
      .catch(error => {
        // 处理错误
        console.error(error);
      });
    // console.log(window.location.pathname)
  }
}



</script>

<template>
  <div style="padding: 10rem;padding-bottom: 0;">
  <el-breadcrumb separator="/">
    <el-breadcrumb-item ><a href="/filesystem/page">promotion management</a></el-breadcrumb-item>
    <el-breadcrumb-item
      ><a href="/filesystem/root/page">promotion management</a></el-breadcrumb-item
    >
    <el-breadcrumb-item>promotion list</el-breadcrumb-item>
    <el-breadcrumb-item>promotion detail</el-breadcrumb-item>
  </el-breadcrumb></div>
</template>

<style scoped>

</style>
