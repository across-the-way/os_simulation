<script setup>
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
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
                    this.pcb = []

                    this.pcb = response.data; // 更新响应数据
                    // this.pcb.forEach(item => {
                    //     for (const key in item) {
                    //         if (item.hasOwnProperty(key)) {
                    //             // 如果属性对应的数组不存在，则创建一个空数组
                    //             if (!pcb[key]) {
                    //                 pcb[key] = [];
                    //             }
                    //             // 将属性值添加到对应的数组中
                    //             pcb[key].push(item[key]);
                    //         }
                    //     }
                    // });
                    // this.pcb = pcb
                    console.log(this.pcb)
                })
                .catch(error => {
                    console.error(error);
                });
        },
        


    },
    mounted() {
        setInterval(this.fetchData, 1000); // 每秒发送请求
        // this.fetchData
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
        <!-- <el-table :data="pcb" height="250">
            <el-table-column v-for="column in columns" 
                :key="column.props" 
                :prop="column.props" 
                :label="column.label"
                width="auto">
            </el-table-column>
            
        </el-table> -->
        <el-table :data="pcb" style="width: 100%">
      
      <el-table-column label="p_id" width="100">
        <template #default="scope">
          <el-popover effect="light" trigger="hover" placement="top" width="190">
            <template #default>
              <div>物理地址 {{ scope.row.p_id }}</div>
              <div>{{ scope.row.priority }}</div>
            </template>
            <template #reference>
              <div>{{ scope.row.p_id }}</div>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="pc" width="180">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            {{ scope.row.pc }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="priority" width="180">
        <template #default="scope">
          <el-popover effect="light" trigger="hover" placement="top" width="190">
            <template #default>
              <div>{{ scope.row.p_id }}</div>
              <div>{{ scope.row.priority }}</div>
            </template>
            <template #reference>
              <el-tag>{{ scope.row.priority }}</el-tag>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="state" width="180">
        <template #default="scope">
          <div>{{ scope.row.state }}</div>
        </template>
      </el-table-column>
    </el-table>
    </div>
</template>
<style></style>