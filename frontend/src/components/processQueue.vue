<template>
  <el-auto-resizer>
    <template #default="{ height, width }">
  <el-table-v2 :columns="columns" :data="rowData" :width="width" :height="height" fixed />
</template>
</el-auto-resizer>
</template>

<script setup>
import { serverURL } from '@/configjs/ServerURL';
import axios from 'axios';
</script>

<script>
export default {
  data() {
    return {
      deviceMapping: {
        0: 'Printer',
        1: 'Keyboard',
        2: 'FileRead',
        3: 'FileWrite',
        4: 'ForkWait',
        5: 'Semaphore',
        6: 'device',
      },
      processQueues: [],
      columns: [],
      res: {},
      rowData: [],
    }
  },
  created() {
    this.getData(); // 调用 getData 方法以从后端获取数据
  },


  methods: {
    maxLength(processQueue) {
      return processQueue.reduce((max, item) => {
        const arr = Object.values(item)[0];
        return Math.max(max, arr.length);
      }, 0);
    },

    generateQueueData(processQueue) {
      this.rowData = processQueue.map((item, index) => {
        const key = Object.keys(item)[0];
        const value = item[key];
        const row = { id: `Queue-${index}`, parentId: null };
        this.columns.forEach((column, columnIndex) => {
          row[column.dataKey] = columnIndex === 0 ? key : (value[columnIndex - 1] || '');
        });
        return row;
      });
    },
    transformData(data) {
      let temp = [];
      let filteredQueues = data.Waiting_Queues.slice(0, 7)
      // 添加设备名称行
      // data.Waiting_Queues.forEach((queue, index) => {
        filteredQueues.forEach((queue, index) => {
        let deviceName = this.deviceMapping[index];
        temp.push({ [deviceName]: queue });
      });
      
      // 添加其他队列名称行
      temp.push({ 'Ready_Queue': data.Ready_Queue });
      let tmp1 = []
      let tmp2 = []
      let tmp3 = []
      data.Second_Queue.forEach(item => {
        tmp1.push(item.pid)
      })
      data.Swapped_Ready_Queue.forEach(item => {
        tmp2.push(item.pid)
      })
      data.Swapped_Waiting_Queue.forEach(item => {
        tmp3.push(item.pid)
      })

      temp.push({ 'Second_Queue': tmp1 });
      temp.push({ 'Swapped_Ready_Queue': tmp2 });
      temp.push({ 'Swapped_Waiting_Queue': tmp3 });
      console.log(temp);
      return temp;
    },
    getData() {
      axios.get(serverURL + '/api/process/queue')
        .then(res => {
          this.res = res.data;
          this.processQueues = this.transformData(this.res); // 使用新数据更新 processQueues
          let max = this.maxLength(this.processQueues) + 1;
          this.columns = this.generateColumns(max, { width: 100 }, 'Queue-'); // 根据新数据重新生成 columns
          this.generateQueueData(this.processQueues); // 根据新数据重新生成 rowData
        })
        .catch(error => {
          console.error("There was an error fetching the process queue data:", error);
          // 这里可以处理错误情况，例如设置一些默认值或者显示错误信息
        });
    },
    generateColumns(length, props, prefix = 'Column-') {
      return Array.from({ length }, (_, columnIndex) => ({
        ...props,
        dataKey: `${prefix}${columnIndex}`,
        title: columnIndex === 0 ? 'Queue Name' : `pid`,
        width: columnIndex == 0 ? 250 : 150,
      }));
    },
  }
  }
  </script>