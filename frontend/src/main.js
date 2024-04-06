import './assets/main.css'
import {createRouter , createWebHistory} from 'vue-router';
import { createApp } from 'vue'
import router from './router/index'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

createApp(App).use(router).use(ElementPlus).mount('#app')