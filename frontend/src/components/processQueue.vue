
<template>
  <el-table-v2 :columns="columns" :data="rowData" :width="700" :height="400" fixed />
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
        5: 'Semaphore'
      },
      processQueues: [
        { 'name1': [1, 2] },
        { 'name2': [1, 2] },
        { 'name3': [3, 4, 5] },
      ],
      columns: [1,1,2,3],
      res: {
        Waiting_Queues: [[1,2,3],[1,8],[1,3],[1,4,3],[],[1,8]],
        Second_Queue: [1,3,4],
        Swapped_Ready_Queue: [2,7,5],
        Swapped_Waiting_Queue:[3,5],
        Ready_Queue: [5,1],
        },
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
      
      // 添加设备名称行
      data.Waiting_Queues.forEach((queue, index) => {
        let deviceName = this.deviceMapping[index];
        temp.push({ [deviceName]: queue });
      });
      
      // 添加其他队列名称行
      temp.push({ 'Ready_Queue': data.Ready_Queue });
      temp.push({ 'Second_Queue': data.Second_Queue });
      temp.push({ 'Swapped_Ready_Queue': data.Swapped_Ready_Queue });
      temp.push({ 'Swapped_Waiting_Queue': data.Swapped_Waiting_Queue });

      return temp;
    },
    getData() {
      axios.get(serverURL + '/process/queue')
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
        width: columnIndex === 0 ? 200 : 150,
      }));
    },

  }
}
</script>