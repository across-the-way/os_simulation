<script setup>
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
defineProps({
    processitem: {
        state: {
            type: String
        },
        p_id: {
            type: Number
        },
        pp_id: {
            type: Number
        },
        c_id: {
            type: Object
        },
        priority: {
            type: Number
        },
        maxresourceMap: {
            type: Object
        },
        allocateresourseMap: {
            type: Object
        },
        bursts: {
            type: Object
        },
        pc: {
            type: Number
        },
        waiting_time: {
            type: Number
        },
        lastready_time: {
            type: Number
        },
        memory_allocate: {
            type: Number
        },
        memory_start: {
            type: Number
        },
        waiting_for: {
            type: Number
        },
        FileTable: {
            type: Object
        },
        holdresourceMap: {
            type: Object
        },
    }
})
</script>
<script>
export default {
    data() {
        return {
            instructions: [],//
            FormData: ''//待处理的指令队列
        };
    },
}
</script>
<template>
    <div>
        <el-text class="mx-1">{{ state }}</el-text>&nbsp;
            <el-text class="mx-1" type="primary">{{ p_id }}</el-text>&nbsp;
            <el-text class="mx-1" type="success">{{ pp_id }}</el-text>&nbsp;
            <el-text class="mx-1" type="info">{{ priority }}</el-text>&nbsp;
            <el-text class="mx-1" type="warning">{{ maxresourceMap }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ allocateresourceMap }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ bursts }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ pc }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ waiting_time }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ lastready_time }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ memory_allocate }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ memory_start }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ waiting_for }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ FileTable }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ holdresourceMap }}</el-text>&nbsp;
            
    </div>
    
</template>
<style scoped>
:deep(.my-label) {
  background: var(--el-color-success-light-9) !important;
}
:deep(.my-content) {
  background: var(--el-color-danger-light-9);
}
</style>