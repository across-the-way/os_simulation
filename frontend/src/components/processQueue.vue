<script setup>
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
import ProcessQueueItem from './ProcessQueueItem.vue';
</script>
<script >
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
                waiting_for: -1,
                fileTable: [22,12],
                holdresourceMap: [],
            },
            {
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
                waiting_for: -1,
                fileTable: [],
                holdresourceMap: [],
            },
        ],
            columns: [
                { props: 'state', label: 'state' },
                { props: 'p_id', label: 'p_id' },
                { props: 'pp_id', label: 'state' },
                { props: 'priority', label: 'priority' },
                { props: 'maxresourceMap', label: 'maxresourceMap' },
                { props: 'allocateresourceMap', label: 'allocateresourceMap' },
                { props: 'bursts', label: 'bursts' },
                { props: 'pc', label: 'pc' },
                { props: 'waiting_time', label: 'waiting_time' },
                { props: 'lastready_time', label: 'lastready_time' },
                { props: 'memory_allocate', label: 'memory_allocate' },
                { props: 'memory_start', label: 'memory_start' },
                { props: 'waiting_for', label: 'waiting_for' },
                { props: 'fileTable', label: 'FileTable' },
                { props: 'holdresourceMap', label: 'holdresourceMap' },
            ],
            form: []
        };
    },

    methods: {
        fetchData() {
            axios.get(serverURL + '/process/status')
                .then(response => {

                    // console.log(_data[0].state)
                    // this.pcb = []

                    // this.pcb = response.data; // 更新响应数据
                    this.pcb.forEach(item => {
                        for (const key in item) {
                            if (item.hasOwnProperty(key)) {
                                // 如果属性对应的数组不存在，则创建一个空数组
                                if (!pcb[key]) {
                                    pcb[key] = [];
                                }
                                // 将属性值添加到对应的数组中
                                pcb[key].push(item[key]);
                            }
                        }
                    });
                    this.pcb = pcb
                    console.log(this.pcb)
                })
                .catch(error => {
                    console.error(error);
                });
        },
        transformStructs() {
            // 创建空对象来存储变化后的数组
            const transformedArrays = {};

            // 遍历结构体数组
            this.pcb.forEach(item => {
                for (const key in item) {
                    if (item.hasOwnProperty(key)) {
                        // 如果属性对应的数组不存在，则创建一个空数组
                        if (!transformedArrays[key]) {
                            transformedArrays[key] = [];
                        }
                        // 将属性值添加到对应的数组中
                        transformedArrays[key].push(pcb[key]);
                    }
                }
            });

            // 更新数据
            this.form = transformedArrays;
            console.log(this.form)
        }


    },
    mounted() {
        // setInterval(this.fetchData, 1000); // 每秒发送请求
        // this.transformStructs
    },
    beforeMount() {
        // clearInterval(this.fetchData)
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
        <el-table :data="this.pcb" height="250">
            <el-table-column v-for="column in columns" 
                :key="column.props" 
                :prop="column.props" 
                :label="column.label"
                :width="'auto'">
            </el-table-column>
            
        </el-table>
    </div>
</template>
<style></style>