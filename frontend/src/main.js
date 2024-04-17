import './assets/main.css'
import {createRouter , createWebHistory} from 'vue-router';
import { createApp } from 'vue'
import router from './router/index'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import App from './App.vue'
import Terminal from 'vue-web-terminal'
import 'vue-web-terminal/lib/theme/dark.css'
const app = createApp(App) 
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
app.use(router).use(Terminal).use(ElementPlus).mount('#app')