<script setup>
import axios from 'axios'
import { serverURL } from '@/configjs/ServerURL'
</script>
<script>
export default {
  data() {
    return {
      instructions: [],//指令队列
      FormData: '',//待处理的指令队列
      timer: null,
      pcb: [],
      columns: [
        { props: 'state', label: 'state' },
        { props: 'p_id', label: 'p_id' },
        { props: 'pp_id', label: 'state' },
        { props: 'priority', label: 'priority' },
        { props: 'maxresourceMap', label: 'maxresourceMap' },
        { props: 'allocateresourceMap', label: 'allocateresourceMap' },
        { props: 'bursts', label: 'bursts' },
        { props: 'pc', label: 'pc' },
        { props: 'waiting_time', label: 'waiting_time' },
        { props: 'lastready_time', label: 'lastready_time' },
        { props: 'memory_allocate', label: 'memory_allocate' },
        { props: 'memory_start', label: 'memory_start' },
        { props: 'waiting_for', label: 'waiting_for' },
        { props: 'fileTable', label: 'FileTable' },
        { props: 'holdresourceMap', label: 'holdresourceMap' },
      ],
      device: [],
      burst: null,
      form: [],
      burstTableVisible: false,
      deviceTableVisible: false,
      displayList: [],
    };
  },
  methods: {
    fetchData() {
      this.timer = setInterval(() => {
        console.log("Fetching data")
        axios.get(serverURL + '/api/process/status')
          .then(response => {
            this.pcb = []
            this.pcb = response.data; // 更新响应数据
            console.log(this.pcb)
          })
          .catch(error => {
            console.error(error);
          });
      }, 2000)
    },
    stopfetchData() {
      clearInterval(this.timer);
    },
    handleSet(data) {
      this.dialogTableVisible = true
      this.displayList = data
    },
    handleSet1(pid){
      axios.get(serverURL + '/BurstInfo'+'?pid='+pid)
      .then((res) => {
          // console.log(res.data);
          this.burst = res.data
          console.log(this.burst);
          this.burstTableVisible = true
      })
    },
    handleSet2(pid){
      axios.get(serverURL + '/ProcessResource'+'?pid='+pid)
      .then((res) => {
          // console.log(res.data);
          this.device = []
          this.device.push(res.data) 
          console.log(this.device,'a')
          this.deviceTableVisible = true
      })
    }
  },
  mounted() {
    this.fetchData(); // 每秒发送请求
  },
  beforeUnmount() {
    this.stopfetchData();
  },
}
</script>
<template>
  <div style="display:inline-block ;margin: auto;">
    <el-table :data="pcb" style="width: 100%">
      <el-table-column label="p_id" width="100">
        <template #default="scope">
          <el-popover effect="light" trigger="hover" placement="top" width="190">
            <template #default>
              <div>物理地址 {{ scope.row.p_id }}</div>
              <div>{{ scope.row.priority }}</div>
            </template>
            <template #reference>
              <div>{{ scope.row.p_id }}</div>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="pc" width="180">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            {{ scope.row.pc }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="priority" width="180">
        <template #default="scope">
          <el-popover effect="light" trigger="hover" placement="top" width="190">
            <template #default>
              <div>{{ scope.row.p_id }}</div>
              <div>{{ scope.row.priority }}</div>
            </template>
            <template #reference>
              <el-tag>{{ scope.row.priority }}</el-tag>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="state" width="180">
        <template #default="scope">
          <div>{{ scope.row.state }}</div>
        </template>
      </el-table-column>
      <el-table-column label="burst" width="120">
        <template #default="scope">
          <el-button size="small" @click="handleSet1(scope.row.p_id)">display</el-button>
        </template>
      </el-table-column>
      <el-table-column label="resource" width="120">
        <template #default="scope">
          <el-button size="small" @click="handleSet2(scope.row.p_id)">display</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="burstTableVisible" title="burst" width="600" align-center>
    <el-table :data="burst">
      <el-table-column property="frame_number" label="frame_number" />
      <el-table-column property="instruction.type" label="ins_type" />
      <el-table-column property="instruction.arguments" label="ins_args" />
      <el-table-column property="logic_address" label="logc_addr" />
      <el-table-column property="page_number" label="pg_num" />
      <el-table-column property="physical_address" label="phy_addr" />
    </el-table>
  </el-dialog>
  <el-dialog v-model="deviceTableVisible" title="resource" width="400" align-center>
    <el-table :data="device">
      <el-table-column property="keyboard" label="keyboard" />
      <el-table-column property="file" label="file" />
      <el-table-column property="printer" label="printer" />
      <el-table-column property="device.." label="device" />  
    </el-table>
  </el-dialog>
  </div>
</template>
<style></style>