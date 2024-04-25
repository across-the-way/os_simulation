<script setup>
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
import routes from './router/routes'
import { ElButton } from 'element-plus';
import { 
  TurnOff,
  ArrowRightBold,
  Open
 } from '@element-plus/icons-vue'


</script>

<script>
export default {
  data() {
    return {
      responseData: [],
    }
  },
  methods: {

    handleButtonClick1() {
      // 处理按钮1点击时的逻辑
      console.log("msg")
      axios.get(serverURL + "/start", {})
        .then(response => {
          console.log(response.data)
        }

        )
    },
    handleButtonClick2() {
      // 处理按钮2点击时的逻辑
      console.log("msg")
      axios.get(serverURL + "/stop", {})
        .then(response => {
          console.log(response.data)
        }

        )
    },
    handleButtonClick3() {
      // 处理按钮2点击时的逻辑
      console.log("msg")
      axios.get(serverURL + "/singlepause", {})
        .then(response => {
          console.log(response.data)
        }

        )
    }
  },
  created() {
  
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

<template>

  <!-- <el-row> -->

  <el-container style="width: 100vw;height: 60px;">
    <el-header style="background-color: white;width: 100%;" class="title">
      <div style="display: inline-flex;padding-left: 20px;color: black;"class="left-aligned" >操作系统</div>
      <div style="display: inline-flex;"class="right-aligned"><el-button  @click="handleButtonClick1" circle  style="display: inline-flex;text-align: right;"  type="success" :icon="Open" />
      <el-button @click="handleButtonClick2"  style="display: inline-flex;" type="danger" circle :icon="TurnOff" />
      <el-button  @click="handleButtonClick3" style="display: inline-flex;margin-right: 0;" type="primary"  circle :icon="ArrowRightBold" />
    </div>
      </el-header>
    </el-container>
    <el-container style="height: calc(100vh - 60px);">
    <el-aside width="200px" height="100%"><el-menu default-active="2" class="el-menu-vertical-demo" @open="handleOpen"
        @close="handleClose" style="width: 200px;height: 100%;;display: flex ;flex-direction: column; margin-left: 0;">
        <el-sub-menu index="/process">
          <template #title>
            <el-icon>
              <location />
            </el-icon>
            <span>进程</span>
          </template>
          <el-menu-item-group title="分类">
            <el-menu-item><router-link to="/process/queue">进程详情</router-link></el-menu-item>
            <el-menu-item index="/find"><router-link to="/process">创建进程（命令行）</router-link></el-menu-item>
            <el-menu-item index="1-2"><router-link to="/process/create/gui">创建进程（图形化）</router-link></el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Group Two">
            <el-menu-item index="1-3"><router-link to="/device">设备管理</router-link></el-menu-item>
          </el-menu-item-group>
          
            <el-menu-item index="1-4"><router-link to="/memory">内存情况</router-link></el-menu-item>
        
        </el-sub-menu>
        <el-menu-item index="1">
          <el-icon><document /></el-icon>
          <router-link to="/filesystem">文件系统</router-link>
        </el-menu-item>
        <el-menu-item index="3">
          <el-icon>
            <document />
          </el-icon>
          <router-link to="/test">测试</router-link>
        </el-menu-item>
        <el-menu-item index="4">
          <el-icon>
            <setting />
          </el-icon>
          <router-link to="/terminal">终端</router-link>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main><router-view></router-view></el-main>
  </el-container>


</template>

<style scoped>
.title {
  width: 100vw;
  /* height: auto; */
  background-color: blanchedalmond;
  /* margin:0 auto; */

  display: flex;
  justify-content: space-between;
  /* padding: 16px; */
  /* padding-left: 40px; */
  text-align: center;
  align-items: center;
}
</style>
