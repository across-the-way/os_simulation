<template>
  <div style="height: 50vh;width: 50vw;margin: auto;align-items: center;">
    <Pie :data="data" :options="options" />
  </div>
  <el-table :data="tableData" style="width: 100%">
    <el-table-column fixed prop="date" label="Date" width="150" />
    <el-table-column prop="name" label="Name" width="120" />
    <el-table-column prop="state" label="State" width="120" />
    <el-table-column prop="city" label="City" width="120" />
    <el-table-column prop="address" label="Address" width="600" />
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
import { Pie } from 'vue-chartjs'
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
      data: {
        labels: ['VueJs', 'EmberJs', 'ReactJs', 'AngularJs', 'JavaScript'],
        memory:{
          strategy: 'LRU',
          free_pages: {},
          lru_caches: [],
          swaped_page_count: 0,
          swaped_used_count: 0,
          used_pages: [{}],
        },
        datasets: [
          {
            backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#6a3f9b'],
            data: [40, 20, 80, 10, 20]
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      },
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
  created(){
    this.fetchdata()
  },
  methods: {
    fetchdata(){
      axios.get(serverURL+'/memory')
      .then(response => {
        console.log(response.data)
      })
    }
  },
}
</script>