<script setup>
import HelloWorld from './components/HelloWorld.vue'
import TheWelcome from './components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from './components/ServerURL'
import routes from './router/routes'
</script>

<script>
export default {
  data() {
    return {
      responseData: null,
    }
  },
  created() {
    // axios.post(serverURL + '/process', 
      
    //     [{
    //     type : 'Memory',
    //     arguments: [111],
    //     },
    //     {
    //     type : 'Priority',
    //     arguments: [2],
    //     },{
    //     type : 'Calculate',
    //     arguments: [300],
    //     },{
    //     type : 'Exit',
    //     arguments: [],
    //     },]
      
      
    // )
    //   .then(response => {
    //     // 处理响应结果
    //     console.log(response.data);
    //     this.responseData = response.data
    //   })
    //   .catch(error => {
    //     // 处理错误
    //     console.error(error);
    //   });
  }
}



</script>

<template><el-col :span="12">
<header>
    
      <el-menu
        default-active="2"
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
        style="width: 200px;height: 100vh;display: inline-block;"
      >
        <el-sub-menu index="/process">
          <template #title>
            <el-icon><location /></el-icon>
            <span>process</span>
          </template>
          <el-menu-item-group title="分类">
            <el-menu-item ><router-link to="/process/queue">queue</router-link></el-menu-item>
            <el-menu-item index="/find"><router-link to="/device">index</router-link></el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Group Two">
            <el-menu-item index="1-3">item three</el-menu-item>
          </el-menu-item-group>
          <el-sub-menu index="1-4">
            <template #title>item four</template>
            <el-menu-item index="1-4-1">item one</el-menu-item>
          </el-sub-menu>
        </el-sub-menu>
        <el-menu-item index="/filesystem">
          <el-icon><icon-menu /></el-icon>
          <router-link to="/filesystem">filesystem</router-link>
        </el-menu-item>
        <el-menu-item index="3" disabled>
          <el-icon><document /></el-icon>
          <span>Navigator Three</span>
        </el-menu-item>
        <el-menu-item index="4">
          <el-icon><setting /></el-icon>
          <span>Navigator Four</span>
        </el-menu-item>
      </el-menu>
    
  </header></el-col>
    
    <main style="display: inline-block;"><router-view style="height: 100vh;"></router-view></main>
  
    
    
  
</template>

<style scoped>




@media (min-width: 1024px) {
  header {
    
    /* place-items: center; */
    /* padding-right: calc(var(--section-gap) / 2); */
    margin-left: 0;
  }

  

 
}
</style>
