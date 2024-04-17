<script setup>

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