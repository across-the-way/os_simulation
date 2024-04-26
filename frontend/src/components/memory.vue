<template>
  <div style="height: 50vh;width: 50vw;margin: auto;align-items: center;">
  <Pie :data="data" :options="options" /></div>
</template>

<script setup>
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Pie } from 'vue-chartjs'
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
        labels: ['VueJs', 'EmberJs', 'ReactJs', 'AngularJs','JavaScript'],
        datasets: [
          {
            backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16','#6a3f9b'],
            data: [40, 20, 80, 10,20]
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    }
  }
}
</script>