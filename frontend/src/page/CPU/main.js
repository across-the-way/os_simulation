import '@/assets/main.css'
// import VueRouter from 'vue-router';
import { createApp } from 'vue'
import App from './cpu.vue'

// Vue.use(VueRouter);
// const router = new VueRouter({
//     routes: [
//       // 定义路由配置
//     ],
//   });
  
//   new Vue({
//     router,
//     render: (h) => h(App),
//   }).$mount('#app');
createApp(App).mount('#app')
