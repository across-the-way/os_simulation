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
  <div>
    欢迎
  </div>

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
