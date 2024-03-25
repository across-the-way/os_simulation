<script setup>
import HelloWorld from './components/HelloWorld.vue'
import TheWelcome from './components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from './components/ServerURL'
import routes from './router/routes'
</script>

<script>
export default {
  data() {
    return {
      responseData: null,
    }
  },
  created() {
    axios.post(serverURL + '/process', 
      
        [{
        type : 'Memory',
        arguments: [111],
        },
        {
        type : 'Priority',
        arguments: [2],
        },{
        type : 'Calculate',
        arguments: [300],
        },{
        type : 'Exit',
        arguments: [],
        },]
      
      
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
  <header>
    <router-link to="/filesystem">filesystem</router-link>
    <router-link to="/cpu">index</router-link>
    <router-link to="/device">index</router-link>
    <router-link to="/process">process</router-link>
    
  </header>
    
  <main>
    <router-view></router-view>
    <TheWelcome />
  </main>
</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
