<script setup>
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
import ProcessQueueItem from './ProcessQueueItem.vue'
</script>
<script>
export default {
    data() {
        return {
            instructions: [],//指令队列
            FormData: '',//待处理的指令队列
            pcb: [{
                state: '1',
                p_id: 2,
                pp_id: 2,
                priority: 10,
                maxresourceMap: [],
                allocateresourceMap: [],
                bursts: [],
                pc: 2,
                waiting_time: 2,
                lastready_time: 5,
                memory_allocate: 43,
                memory_start: 0,
                waiting_for : -1,
                FileTable: [],
                holdresourceMap: [],
            }],
        };
    },

    methods: {
        fetchData() {
            axios.get(serverURL + '/process/status')
                .then(response => {

                    // console.log(_data[0].state)
                    this.pcb = []

                    this.pcb = response.data; // 更新响应数据

                    console.log(this.pcb)
                })
                .catch(error => {
                    console.error(error);
                });
        }

    },
    // mounted() {
    //     // setInterval(this.fetchData, 1000); // 每秒发送请求
    // }
    beforeMount() {
        clearInterval(this.fetchData)
    }
    // updated() {
    //     axios.post(serverURL + '/process/instructions',this.formData)
    //         .then(response => {
    //             // 处理响应结果
    //             console.log(response.data);
    //             this.responseData = response.data
    //         })
    //         .catch(error => {
    //             // 处理错误
    //             console.error(error);
    //         });
    // }
}
</script>
<template>
    <div style="display:inline-block ;">
        <form @submit.prevent="handleSubmit">
            <label for="name">{{ this.pcb }}</label>
            <br>
            <el-text class="mx-1">{{ this.pcb[0].state }}</el-text>&nbsp;
            <el-text class="mx-1" type="primary">{{ this.pcb[0].p_id }}</el-text>&nbsp;
            <el-text class="mx-1" type="success">{{ this.pcb[0].pp_id }}</el-text>&nbsp;
            <el-text class="mx-1" type="info">{{ this.pcb[0].priority }}</el-text>&nbsp;
            <el-text class="mx-1" type="warning">{{ this.pcb[0].maxresourceMap }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].allocateresourceMap }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].bursts }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].pc }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].waiting_time }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].lastready_time }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].memory_allocate }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].memory_start }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].waiting_for }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].FileTable }}</el-text>&nbsp;
            <el-text class="mx-1" type="danger">{{ this.pcb[0].holdresourceMap }}</el-text>&nbsp;
            <br>
        </form>
        <el-table :data="tableData" height="250" style="width: 100%">
            <el-table-column prop="date" label="Date" width="180" />
            <el-table-column prop="name" label="Name" width="180" />
            <el-table-column prop="address" label="Address" />
        </el-table>
        <ProcessQueueItem></ProcessQueueItem>
    </div>
</template>
<style></style>