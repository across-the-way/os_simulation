<!--
<template>
  <el-table-v2 :columns="columns" :data="rowData" :width="700" :height="400" fixed />
</template>

<script setup>
import { serverURL } from '@/configjs/ServerURL';
import axios from 'axios';

// 定义映射表

//const processQueue = getData();


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
        Waiting_Queues: [[1],[1],[1],[1],[1],[1]],
        Second_Queue: [1],
        Swapped_Ready_Queue: [2],
        Swapped_Waiting_Queue:[3],
        Ready_Queue: [5],
        },
      rowData: [],
    }
  },
  created() {
    this.getData()
    let max = this.maxLength(this.processQueues) + 1
    this.columns = this.generateColumns(max,{ width: 100 }, 'Queue-')
    this.generateQueueData(this.processQueues, this.columns);
  },


  methods: {
    maxLength(processQueue) {
      processQueue.reduce((max, item) => {
        const arr = Object.values(item)[0];
        return Math.max(max, arr.length);
      }, 0)
    },

    generateQueueData(processQueue, columns, prefix = 'Queue-') {
      processQueue.map((item, rowIndex) => {
        const name = Object.keys(item)[0];
        const queue = item[name];
        const rowData = columns.reduce((rowData, column, columnIndex) => {
          if (columnIndex === 0) {
            rowData[column.dataKey] = name;
          } else {
            rowData[column.dataKey] = queue[columnIndex - 1] !== undefined ? ` ${queue[columnIndex - 1]}` : '';
          }
          return rowData;
        }, { id: `${prefix}${rowIndex}`, parentId: null });

        this.rowData = rowData
      })

    },
    getData() {
      axios.get(serverURL + '/process/queue')
        .then(res => {
          let temp = []
          this.res = res.data
          let wait = this.res.Waiting_Queues
          let ready = this.res.Ready_Queue
          let sq = this.res.Second_Queue
          let srq = this.res.Swapped_Ready_Queue

          // 使用映射表转换索引为设备名
          wait.forEach(wa => {
            let name = deviceMapping[wa]; // 使用映射表查找对应的设备名称
            temp.push({ name: wa });
          });

          temp.push({ 'Ready_Queue': ready });
          temp.push({ 'Second_Queue': sq });
          temp.push({ 'Swapped_Ready_Queue': srq });
          temp.push({ 'Swapped_Waiting_Queue': swq });
          this.processQueues = temp

        });
    },
    generateColumns(length, props, prefix = 'columns-') {
      Array.from({ length }).map((_, columnIndex) => ({
        ...props,
        // key: `${prefix}${columnIndex}`,
        dataKey: `${prefix}${columnIndex}`,
        title: columnIndex === 0 ? 'Name' : `Column ${columnIndex - 1}`,
        width: columnIndex === 0 ? 200 : 150,
      }))
    },

  }
}
</script>
-->
<template>
  <el-table-v2 :columns="columns" :data="rowData" :width="700" :height="400" fixed />
</template>

<script setup>
import { serverURL } from '@/configjs/ServerURL';
import axios from 'axios';

// 定义映射表

//const processQueue = getData();


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
        Waiting_Queues: [[1,2,3],[1],[1,3],[1,4,3],[1],[1,8]],
        Second_Queue: [1,3,4],
        Swapped_Ready_Queue: [2,7,5],
        Swapped_Waiting_Queue:[3,5],
        Ready_Queue: [5,1],
        },
      rowData: [],
    }
  },
  created() {
    // 不需要调用 this.getData() 因为 res 数据已经提供
    this.processQueues = this.transformData(this.res);
    let max = this.maxLength(this.processQueues) + 1;
    this.columns = this.generateColumns(max, { width: 100 }, 'Queue-');
    this.generateQueueData(this.processQueues);
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
          this.res = res.data
          console.log(res.data)
        });
    },
    generateColumns(length, props, prefix = 'Column-') {
      return Array.from({ length }, (_, columnIndex) => ({
        ...props,
        dataKey: `${prefix}${columnIndex}`,
        title: columnIndex === 0 ? 'Device Name' : `Column ${columnIndex - 1}`,
        width: columnIndex === 0 ? 200 : 150,
      }));
    },

  }
}
</script>