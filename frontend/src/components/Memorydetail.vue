<template>
    
    键盘：{{ res.keyboard }}<br>
    文件：{{ res.file }}<br>
    打印机：{{ res.printer }}<br>
    <!-- {{ info }}<br> -->
    页号：{{ abstract.page_num }}<br>
    帧号：{{ abstract.frame_number }}<br>
    <el-button @click="fetchdata">按地址详情</el-button>
    <el-dialog v-model="dialogTableVisible" title="块内详情" width="800">
        <el-table :data="info">
            <el-table-column property="instruction.type" label="指令"  />
            <el-table-column  property="instruction.arguments" label="参数"  >
            </el-table-column>
            <el-table-column property="logic_address" label="逻辑地址" />
            <el-table-column property="physical_address" label="物理地址" />
            <el-table-column property="frame_number" label="帧号" />
            <el-table-column property="page_number" label="页号" />
        </el-table>
    </el-dialog>
</template>
<script setup>
import axios from "axios"
import { serverURL } from '@/configjs/ServerURL'
defineProps({
    msg: {
        type: Number,
        required: true
    }
})
</script>
<script>
export default {
    data() {
        return {
            info: null,
            abstract: {
                page_num: -1,
                frame_number: -1
            },
            res: {
                keyboard: 0,
                file: 0,
                printer: 0,
            },
            dialogTableVisible: false,
        }
    },
    mounted() {
        this.fetchdata1();
        this.fetchdata2()
    },
    methods: {
        fetchdata() {
            axios.get(serverURL + '/FrameInfo' + '?frame_num=' + this.msg)
                .then(res => {
                    this.info = []
                    res.data.forEach(ins => {
                        // ins.forEach(item => {
                            this.info.push(ins)

                        // })
                    });
                    this.dialogTableVisible = true
                    console.log(res.data)
                })
        },
        fetchdata1() {
            axios.get(serverURL + '/FrameAbstract' + '?frame_num=' + this.msg)
                .then(res => {
                    this.abstract = res.data
                    console.log(res.data)
                })
        }, fetchdata2() {
            axios.get(serverURL + '/ProcessResource' + '?pid=1')
                .then(res => {
                    this.res = res.data
                    console.log(res.data)
                })
        },
    }
}
</script>