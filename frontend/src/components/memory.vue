<template>
  <div style="height: 50vh;width: 50vw;margin: auto;align-items: center;">
    <Doughnut :data="data" :options="options" :key="componentkey"/>
  </div>
  <el-table :data="tableData" style="width: 100%;margin-top: 20px;">
    <el-table-column fixed prop="date" label="Date" width="150" />
    <el-table-column prop="name" label="Name" width="120" />
    <el-table-column prop="state" label="State" width="120" />
    <el-table-column prop="city" label="City" width="120" />
    <el-table-column prop="address" label="Address" width="200" />
    <el-table-column prop="zip" label="Zip" width="120" />
    <el-table-column fixed="right" label="Operations" width="120">
      <template #default>
        <el-button link type="primary" size="small" @click="handleClick">
          Detail
        </el-button>
        <el-button link type="primary" size="small">Edit</el-button>
      </template>
    </el-table-column>
  </el-table>
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

  components: {
    Pie
  },
  data() {
    return {
      componentkey: 0,
      data: {
        labels: ['VueJs'],

        datasets:
          [{
            backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#6a3f9b'],
            data: []
          }]

      },
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
        strategy: '',
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
    console.log(this.temp)
    // this.paintcon()
    // .then()
    
  },
  computed: {
    mappedItems() {
      return Object.entries(this.mapper).map(([key, value]) => ({
        key,
        value
      }));
    }
  },
  methods: {
    fetchdata() {
      axios.get(serverURL + '/memory')
        .then(response => {
          console.log(response.data)
          if (this.continueallocate.includes(response.data.strategy)) {
            this.memory = response.data
            this.memory.used_memory.forEach(memory => {
              this.data.datasets[0].data.push(memory.size)
              this.data.labels.push('pid-' + memory.key)
              // console.log(this.data.datasets[0].data)
            })
            this.data.datasets[0].data.push(this.memory.free_blocks[0].size)
            //将数据处理好塞到data中
            //
          }
          else {
            this.details = response.data.details
            let temp =[]
            this.temp = Object.entries(this.details.used_pages).map(([key, value]) => ({
              key,
              value
            }))
            console.log(this.temp)
            console.log(this.data)
            this.temp.forEach(item=>{
              console.log(item.key)
              console.log(item.value.size)
              temp.push(item.value.size)
              let data = item.key
              
              this.data.labels.push('pid-'+data)
            })
            console.log(temp)
            this.data.datasets[0].data = temp
            console.log(this.data,'a')
            this.componentkey++
            // console.log(this.details.used_pages[1].size)
            // this.data.label.push("data")
            // this.data.datasets.data.push(1)
            // this.details.used_pages.forEach(page => 
            // {
            // this.data.labels.push('page-' + page.key)
            // this.data.datasets[0].data.push(page.size)
            // console.log(page)
            // })
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
