<script setup>
import axios from 'axios'
import { serverURL } from '@/configjs/ServerURL'

</script>

<script>
export default {
  data() {
    return {
      responseData: null,
      form: {
        cpu: null,
        memory: null,
      }
    }
  },
  created() {
    // axios.get(serverURL + '/', {})
    //   .then(response => {
    //     // 处理响应结果
    //     console.log(response.data);
    //     this.responseData = response.data
    //   })
    //   .catch(error => {
    //     // 处理错误
    //     console.error(error);
    //   });
  },
  methods: {
    handSubmit1() {
      axios.post(serverURL + '/resetMemoryStrategy', form.memory)
        .then(res => {
          console.log(res.data);

        })
        .catch(error => {
          console.log(error);
        });

    },
    handleSubmit2() {
      axios.post(serverURL + '/resetCPUStrategy', form.cpu)
        .then(res => {
          console.log(res.data);

        })
        .catch(error => {
          console.log(error);
        });
    },
    handleSubmit3() {
      axios.get(serverURL + '/restart')
        .then(res => {
          console.log(res.data);
        })
        .catch(err => {
          console.log(err);
        })
      localStorage.setItem('config', JSON.stringify(this.form))
    },
    ondelete() {
      localStorage.clear()
    },
  }
}



</script>

<template>
  <el-form :model="form" label-width="auto" style="max-width: 600px;margin: auto;">

    <el-form-item label="memory distributing method" >
      <el-select v-model="form.memory" placeholder="please select your memory method">
        <el-option label="FCFS" value="shanghai" />
        <el-option label="Zone two" value="beijing" />

      </el-select>
      
    </el-form-item>
<el-button type="primary" @click="handSubmit1" class="button">Create</el-button>
    <el-form-item label="process dealing method">
      <el-select v-model="form.cpu" placeholder="please select your process method">
        <el-option label="Zone one" value="shanghai" />
        <el-option label="Zone two" value="beijing" />
      </el-select>
    </el-form-item>

    <el-form-item class="button">
      <el-button type="primary" @click="handleSubmit2">Create</el-button>
      <el-button @click="handleSubmit3">restart</el-button>
    </el-form-item>
  </el-form>
  <el-button type="primary" @click="ondelete">clear</el-button>

</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}
.button {
  margin-left: 260px;
}

</style>
