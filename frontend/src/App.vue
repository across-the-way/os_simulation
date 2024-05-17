<script setup>
import axios from 'axios'
import { serverURL } from '@/configjs/ServerURL'
import routes from './router/routes.js'
import { ElButton } from 'element-plus';
import {
  TurnOff,
  ArrowRightBold,
  Open,
  RefreshRight
} from '@element-plus/icons-vue'


</script>

<script>
export default {
  data() {
    return {
      responseData: [],
      cpu: '',
      memory: '',
      type1: [
        { label: 'FirstFit', value: 'FirstFit' },
        {label: 'NextFit', value: 'NextFit'},
        { label: 'BestFit', value: 'BestFit' },
        { label: 'WorstFit', value: 'WorstFit' },
        { label: 'Page', value: 'Page' },
        { label: 'LRU', value: 'LRU' },
        { label: 'FIFO', value: 'FIFO' },
      ],
      type0: [
        { label: 'FCFS', value: 'FCFS' },
        { label: 'SJF', value: 'SJF' },
        { label: 'Priority', value: 'Priority' },
        { label: 'RR', value: 'RR' },
        { label: 'MLFQ', value: 'MLFQ' },
      ],
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
    },
    handleButtonClick4() {
      // 处理重启按钮点击时的逻辑
      console.log("msg")
      axios.get(serverURL + '/restart')
    },
    cpuTypeChanged(type){
      axios.get(serverURL + '/resetCPUStrategy'+'?strategy='+ type)
    },
    memoryStrategyChanged(type){
      axios.get(serverURL + '/resetMemoryStrategy'+ '?strategy=' +type)
    },
  },
}



</script>

<template>

  <!-- <el-row> -->

  <el-container style="width: 100vw;height: 60px;">
    <el-header style="background-color: white;width: 100%;" class="title">
      <div style="display: inline-flex;padding-left: 20px;color: black;" class="left-aligned">操作系统</div>
      <div style="display: inline-flex;" class="right-aligned">
        <div style="margin-top: 5px;">模式选择：</div>

        <el-select v-model="cpu" style="width: 120px;margin-right: 10px" placeholder="process"@change="cpuTypeChanged(cpu)">
          <el-option v-for="option in type0" :label="option.label" :value="option.value"></el-option>
        </el-select>
        <el-select v-model="memory" style="width: 120px;margin-right: 10px" placeholder="memory"@change="memoryStrategyChanged(memory)">
          <el-option v-for="option in type1" :label="option.label" :value="option.value"></el-option>
        </el-select>
        <el-button @click="handleButtonClick4" circle style="display: inline-flex;text-align: right;" type="warning"
          :icon="RefreshRight" />
        <el-button @click="handleButtonClick1" circle style="display: inline-flex;text-align: right;" type="success"
          :icon="Open" />
        <el-button @click="handleButtonClick2" style="display: inline-flex;" type="danger" circle :icon="TurnOff" />
        <el-button @click="handleButtonClick3" style="display: inline-flex;margin-right: 0;" type="primary" circle
          :icon="ArrowRightBold" />
      </div>
    </el-header>
  </el-container>
  <el-container style="height: calc(100vh - 60px);">
    <el-aside width="200px" height="100%"><el-menu default-active="2" class="el-menu-vertical-demo" @open="handleOpen"
        @close="handleClose" style="width: 200px;height: 100%;;display: flex ;flex-direction: column; margin-left: 0;">
        <el-sub-menu index="/process">
          <template #title>
            <el-icon>
              <Files />
            </el-icon>
            <span>进程</span>
          </template>
          
            <el-menu-item><router-link to="/process">进程详情</router-link></el-menu-item>
            <el-menu-item><router-link to="/process/queue">进程队列</router-link></el-menu-item>
            <el-menu-item index="/find"><router-link to="/process/create/cmd">创建进程（命令行）</router-link></el-menu-item>
            <el-menu-item index="1-2"><router-link to="/process/create/gui">创建进程（图形化）</router-link></el-menu-item>
          

        </el-sub-menu>
        <el-menu-item index="1">
          <el-icon>
            <monitor />
          </el-icon>
          <router-link to="/memory">内存情况</router-link>
        </el-menu-item>
        <el-menu-item index="2">
          <el-icon>
            <SetUp />
          </el-icon>
          <router-link to="/device">设备管理</router-link>
        </el-menu-item>
        <el-menu-item index="3">
          <el-icon>
            <document />
          </el-icon>
          <router-link to="/filesystem">文件系统</router-link>
        </el-menu-item>
        <el-menu-item index="6">
          <el-icon>
            <Operation />
          </el-icon>
          <router-link to="/test">测试</router-link>
        </el-menu-item>
        <el-menu-item index="4">
          <el-icon>
            <Switch />
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
