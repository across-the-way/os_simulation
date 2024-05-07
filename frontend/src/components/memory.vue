<template>
  <div style="height: 50vh;width: 50vw;margin: auto;align-items: center;">
    <Doughnut :data="data" :options="options" :key="componentkey"/>
  </div>
  
  <el-descriptions title="User Info" style="max-width: 800px;margin: auto;background-color: white;padding: 20px;margin-top: 20px;" v-if="ioperation == 2 && strategy != 'Page'">
    <!-- <el-descriptions-item label="Username">{{ details }}</el-descriptions-item> -->
    <el-descriptions-item label="Telephone">{{ details.free_pages }}</el-descriptions-item>
    <el-descriptions-item label="Place">{{ details.lru_cache }}</el-descriptions-item>
    <el-descriptions-item label="Remarks">
      <el-tag size="used_page">{{ temp }}</el-tag>
    </el-descriptions-item>
    <el-descriptions-item label="Address">
      {{ strategy }}
    </el-descriptions-item>
    <el-descriptions-item label="Address">
      {{ details.pages }}
    </el-descriptions-item>
    <el-descriptions-item label="Address">
      {{ details.faults }}
    </el-descriptions-item>
    <el-descriptions-item label="Address">
      {{ details.swaped_page_count }}

    </el-descriptions-item>
    <el-descriptions-item label="Address">
      {{ details.swaped_used_count }}
    </el-descriptions-item>
  </el-descriptions>
  <el-descriptions title="User Info" style="max-width: 800px;margin: auto;background-color: white;padding: 20px;margin-top: 20px;" v-else-if=" strategy == 'Page'">
    <!-- <el-descriptions-item label="Username1">{{ this.details.used_pages }}</el-descriptions-item> -->
    <el-descriptions-item label="Telephone">{{ this.details.free_pages }}</el-descriptions-item>
    <el-descriptions-item label="Remarks">
      <el-tag size="small">{{ temp }}</el-tag>
    </el-descriptions-item>
    <el-descriptions-item label="Address">
      {{ strategy }}
    </el-descriptions-item>
  </el-descriptions>
  <el-descriptions title="User Info2" style="max-width: 800px;margin: auto;background-color: white;padding: 20px;margin-top: 20px;" v-else>
    <el-descriptions-item label="Username">{{ memory }}</el-descriptions-item>
    <el-descriptions-item label="Telephone">{{ this.memory.free_blocks }}</el-descriptions-item>
    <!-- <el-descriptions-item label="Place">{{ this.memory.free_pages }}</el-descriptions-item> -->
    <el-descriptions-item label="Remarks">
      <el-tag size="small">{{ memory.used_memory }}</el-tag>
    </el-descriptions-item>
    <el-descriptions-item label="Remarks">
      <el-tag size="small">{{ temp }}</el-tag>
    </el-descriptions-item>
    <el-descriptions-item label="Address">
      {{ strategy }}
    </el-descriptions-item>
  </el-descriptions>
</template>

<script setup>
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut, Pie } from 'vue-chartjs'
import axios from 'axios';
import { serverURL } from '@/configjs/ServerURL';
ChartJS.register(ArcElement, Tooltip, Legend)
</script>

<script>
export default {

  // components: {
  //   Pie
  // },
  data() {
    return {
      componentkey: 0,
      data: {
        labels: [],
        datasets:
          [{
            backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#6a3f9b'],
            data: []
          }]
      },
      strategy: '',
      page: false,
      memory: {
        strategy: '',
        free_blocks: [{
          start: 2200,
          end: 4095,
          size: 1896,
        }],
        used_memory: [
          {
            start: 0,
            end: 199,
            size: 200,
          },
          {
            start: 200,
            end: 2199,
            size: 2000,
          }],
      },
      details: {
        faults: 0,
        // strategy: '',
        free_pages: {
          empty: false,
        },
        lru_cache: [{
          page_num_physical: 2,
          pid: 0,
          page_num_virtual: 0,
        }],
        pages: 0,
        swaped_page_count: 0,
        swaped_used_count: 0,
        used_pages: [
        ],

      },
      continueallocate: [
        'FirstFit',
        'NextFit',
        'BestFit',
        'WorstFit',],
      pageallocate: [
        'Page',
        'LRU',
        'FIFO',],
      options: {
        responsive: true,
        maintainAspectRatio: false
      },
      temp: null,
      ioperation: 0,
      tableData: [
        {
          date: '2016-05-03',
          name: 'Tom',
          state: 'California',
          city: 'Los Angeles',
          address: 'No. 189, Grove St, Los Angeles',
          zip: 'CA 90036',
          tag: 'Home',
        },
        {
          date: '2016-05-02',
          name: 'Tom',
          state: 'California',
          city: 'Los Angeles',
          address: 'No. 189, Grove St, Los Angeles',
          zip: 'CA 90036',
          tag: 'Office',
        },
        {
          date: '2016-05-04',
          name: 'Tom',
          state: 'California',
          city: 'Los Angeles',
          address: 'No. 189, Grove St, Los Angeles',
          zip: 'CA 90036',
          tag: 'Home',
        },],
    }
  },
  created() {
    this.fetchdata()
    // console.log(this.temp)
    // this.paintcon()
    // .then()
    
  },
  
  methods: {
    fetchdata() {
      axios.get(serverURL + '/memory')
        .then(response => {
          console.log(response.data)
          if (this.continueallocate.includes(response.data.strategy)) {
            this.ioperation = 1
            console.log(response.data)
            this.memory = response.data.details
            this.strategy = response.data.strategy
            this.temp = Object.entries(this.memory.used_memory).map(([key, value]) => ({
              key,
              value
            }))
            // this.temp = []
            let temp = []
            this.memory.free_blocks.forEach((block)=>{
              this.temp.push({key:'free',value:block})
            })
            this.temp.sort((a, b) =>a.value.start - b.value.start)
            this.temp.forEach(item=>{
              console.log(item.key)
              console.log(item.value.size)
              temp.push(item.value.size)
              let data = item.key
              if(item.key === 'free'){
                this.data.labels.push(data)
              }
              else
              this.data.labels.push('pid-'+data)
            })
            console.log(temp)
            this.data.datasets[0].data = temp
            
            this.componentkey++
            // this.memory.used_memory.forEach(memory => {
            //   this.data.datasets[0].data.push(memory.size)
            //   this.data.labels.push('pid-' + memory.key)
            //   // console.log(this.data.datasets[0].data)
            // })
            // this.data.datasets[0].data.push(this.memory.free_blocks[0].size)
            //将数据处理好塞到data中
            //
          }
          else {
            this.ioperation = 2
            this.details = response.data.details
            this.strategy = response.data.strategy
            let temp =[]
            this.temp = Object.entries(this.details.used_pages).map(([key, value]) => ({
              key,
              value
            }))
            console.log(this.temp)
            console.log(this.data)
            let sum = 0
            this.temp.forEach(item=>{
              console.log(item.key)
              console.log(item.value.size)
              temp.push(item.value.size)
              let data = item.key
              sum += item.value.size
              this.data.labels.push('pid-'+data)
            })
            console.log(temp)
            temp.push(4096-sum)
            this.data.labels.push('free')
            this.data.datasets[0].data = temp
            console.log(this.data,'a')
            this.componentkey++
            //将数据处理好塞入data中
          }
        })
    },
    paintcon() {
      this.memory.used_memory.forEach(memory => {
        this.data.datasets[0].data.push(memory.size)
        this.data.labels.push('memory.key')
        // console.log(this.data.datasets[0].data)
      })
      this.data.datasets[0].data.push(this.memory.free_blocks[0].size)
      // this.data.datasets[0].data.push(this.temp[0].value.size)
    }
  },
}
</script>
