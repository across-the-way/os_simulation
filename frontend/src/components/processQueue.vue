<template>
  <el-table-v2 :columns="columns" :data="rowData" :width="700" :height="600" fixed />
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
      processQueues1: [],
      columns: [1, 1, 2, 3],
      res: {
        Ready_Queue: [5, 1],
        Second_Queue: [1, 3, 4],
        Swapped_Ready_Queue: [2, 7, 5],
        Swapped_Waiting_Queue: [3],

        Waiting_Queues: [[1], [], [], [], [], [], []],
      },
      rowData: [],
    }
  },
  created() {
    // 不需要调用 this.getData() 因为 res 数据已经提供
    axios.get(serverURL + '/process/queue')
      .then(res => {
        this.res = []
        this.res = res.data
        console.log(this.res)
        this.transformData()
        console.log(this.processQueues)
        let max = this.maxLength(this.processQueues) + 2;
        // console.log(max)
        this.columns = this.generateColumns(max, { width: 100 }, 'Queue-');
        this.generateQueueData(this.processQueues);

      })
    // this.getData()
    // this.transformData()
    // let max = this.maxLength(this.processQueues1) + 2;
    // console.log(max)
    // this.columns = this.generateColumns(max, { width: 100 }, 'Queue-');
    // this.generateQueueData(this.processQueues1);
  },
  // updated() {
  //   let max = this.maxLength(this.processQueues1) + 2;
  //   console.log(max)
  //   this.columns = this.generateColumns(max, { width: 100 }, 'Queue-');
  //   this.generateQueueData(this.processQueues1);
  // },

  methods: {
    maxLength(processQueue) {
      console.log('a')
      return processQueue.reduce((max, item) => {
        const arr = Object.values(item)[0];

        return Math.max(max, arr.length);
      }, 0);
    },

    generateQueueData(processQueue) {
      console.log(processQueue)
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
    transformData() {
      let temp = [];
      let data = this.res
      console.log('b')
      // 添加设备名称行
      this.res.Waiting_Queues.forEach((queue, index) => {
        let deviceName = this.deviceMapping[index];
        temp.push({ [deviceName]: queue });
      });

      // 添加其他队列名称行
      temp.push({ 'Ready_Queue': data.Ready_Queue });
      temp.push({ 'Second_Queue': data.Second_Queue });
      temp.push({ 'Swapped_Ready_Queue': data.Swapped_Ready_Queue });
      temp.push({ 'Swapped_Waiting_Queue': data.Swapped_Waiting_Queue });
      this.processQueues = temp
    },
    getData() {
      console.log('this.processQueues')
      axios.get(serverURL + '/process/queue')
        .then(res => {
          this.res = res.data
          console.log(res.data)
          let temp = []
          let data = res.data
          // 添加设备名称行
          this.res.Waiting_Queues.forEach((queue, index) => {
            let deviceName = this.deviceMapping[index];
            temp.push({ [deviceName]: queue });
          });

          // 添加其他队列名称行
          temp.push({ 'Ready_Queue': data.Ready_Queue });
          temp.push({ 'Second_Queue': data.Second_Queue });
          temp.push({ 'Swapped_Ready_Queue': data.Swapped_Ready_Queue });
          temp.push({ 'Swapped_Waiting_Queue': data.Swapped_Waiting_Queue });
          console.log(temp)
          this.processQueues1 = temp
          this.processQueues = temp
          console.log(this.processQueues, this.processQueues1, 'aa')
          

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
</style>
