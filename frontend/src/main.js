import './assets/main.css'
import {createRouter , createWebHistory} from 'vue-router';
import { createApp } from 'vue'
import router from './router/index'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import Terminal from 'vue-web-terminal'
import 'vue-web-terminal/lib/theme/dark.css'

createApp(App).use(router).use(Terminal).use(ElementPlus).mount('#app')