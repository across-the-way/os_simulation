<script setup>
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>
<script>
export default {
    created(){
        axios.post(serverURL + '/process', 
            [{
                type: 'Wait',
            }]
        )
      .then(response => {
        // 处理响应结果
        console.log(response.data);
        this.responseData = response.data
      })
      .catch(error => {
        // 处理错误
        console.error(error);
      });
    }
}
</script>
<template>

</template>
<style>
</style>