<script setup>
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

<template >

  <!-- <el-row> -->
    
    <el-container style="width: 100vw;height: 100vh;">
      <el-header style="background-color: white;width: 100%;"class="title">
        <span style="margin-top: 40px;padding-top: 30px;">操作系统</span>
      </el-header>
      <el-container>
        <el-aside width="200px"><el-menu default-active="2" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose"
      style="width: 200px;height: 100%;;display: flex ;flex-direction: column; margin-left: 0;">
      <el-sub-menu index="/process">
        <template #title>
          <el-icon>
            <location />
          </el-icon>
          <span>进程</span>
        </template>
        <el-menu-item-group title="分类">
          <el-menu-item><router-link to="/process/queue">进程详情</router-link></el-menu-item>
          <el-menu-item index="/find"><router-link to="/process">创建进程</router-link></el-menu-item>
        </el-menu-item-group>
        <el-menu-item-group title="Group Two">
          <el-menu-item index="1-3"><router-link to="/device">设备管理</router-link></el-menu-item>
        </el-menu-item-group>
        <el-sub-menu index="1-4">
          <template #title>item four</template>
          <el-menu-item index="1-4-1">item one</el-menu-item>
        </el-sub-menu>
      </el-sub-menu>
      <el-menu-item index="/filesystem">
        <el-icon><icon-menu /></el-icon>
        <router-link to="/filesystem">文件系统</router-link>
      </el-menu-item>
      <el-menu-item index="3" disabled>
        <el-icon>
          <document />
        </el-icon>
        <span>Navigator Three</span>
      </el-menu-item>
      <el-menu-item index="4">
        <el-icon>
          <setting />
        </el-icon>
        <router-link to="/terminal">终端</router-link>
      </el-menu-item>
    </el-menu></el-aside>
        <el-main><router-view></router-view></el-main>
      </el-container>
    </el-container>
  
</template>

<style scoped>
.title {
  width: 100vw;
  /* height: auto; */
  background-color: blanchedalmond;
  margin: auto;
  padding: 20px;
  padding-left: 40px;
}


</style>
