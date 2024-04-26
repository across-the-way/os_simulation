<template>
    <el-form ref="formRef" style="max-width: 600px" :model="instructionQueue" label-width="auto" class="demo-dynamic">
        <el-form-item prop="memory" label="Memory" :rules="[
            {
                required: true,
                message: 'Please input instructions argus',
                trigger: 'blur',
            },
        ]">
            <el-input v-model="instructionQueue.memory" />
        </el-form-item>
        <el-form-item prop="priority" label="Priority" :rules="[
            {
                required: true,
                message: 'Please input instructions argus',
                trigger: 'blur',
            },
        ]">
            <el-input v-model="instructionQueue.priority" />
        </el-form-item>
        <el-form-item v-for="(domain, index) in instructionQueue.domains" :key="domain.key" :label="domain.type"
            :prop="'domains.' + index + '.arguments'" :rules="{
                required: true,
                message: 'domain can not be null',
                trigger: 'blur',
            }">
            <el-input v-model="domain.arguments" v-if="domain.type != 'Exit'" />
            <el-button class="mt-2" @click.prevent="removeDomain(index)">
                Delete
            </el-button>
        </el-form-item>
        <el-form-item prop="exit" label="Exit" :rules="[
            {
                required: false,
            },
        ]"></el-form-item>
        <el-form-item>
            <el-button type="primary" @click="submitForm(instructionQueue)">Submit</el-button>
            <el-select v-model="selectedDomain" placeholder="Select" style="width: 105px;padding-left: 12px;" @change="addDomain(selectedDomain)">
                <el-option label="New Calc" value="Calculate"></el-option>
                <el-option label="New Exit" value="Exit"></el-option>
                <el-option label="New Domain" value="ins"></el-option>
            </el-select>
        </el-form-item>
    </el-form>
</template>

<script setup>

import axios from 'axios';
import { serverURL } from './ServerURL';

</script>
<script>
export default {
    data() {
        return {
            city: [
                {
                    value: '文件',
                    label: '文件',
                },
                {
                    value: '文件夹',
                    label: '文件夹',
                },
            ],
            instructionQueue: {
                domains: [
                    
                ],
                memory: '',
                priority: 1,
                exit: '',
                index: 1,
                instructiontype: [
                    'Memory',  // 参数：内存大小
                    'AccessMemory',  // 参数：内存地址

                    // 进程管理
                    'Priority',  // 参数：进程优先级
                    'Calculate',  // 参数：计算时间
                    'Fork', // 参数：子进程的入口
                    'Exit',   // 参数：无
                    'Wait', // 参数：无
                    'CondNew', // 参数：信号量名，初始值
                    'CondWait', // 参数：信号量名
                    'CondSignal', // 参数：信号量名

                    // 文件系统
                    'CreateDir', // 参数：父目录路径，目录名
                    'CreateFile', // 参数：目录路径，文件名
                    'DeleteDir', // 参数：目录路径
                    'DeleteFile', // 参数：文件路径
                    'WriteFile', // 参数：文件路径，写入时间
                    'ReadFile', // 参数：文件路径，读取时间
                    'OpenFile', // 参数：文件路径
                    'CloseFile', // 参数：文件号

                    // 设备管理
                    'Printer',  // 参数：使用时间
                    'Keyboard',  // 参数：使用时间
                ],

            },
            selectedDomain: ''
        }
    },
    methods: {
        removeDomain(index) {

            if (index !== -1) {
                this.instructionQueue.domains.splice(index, 1)
                // this.index = this.index - 1
            }
        },
        addDomain(type) {
            if (type) {
                this.instructionQueue.domains.push({
                    // key: this.index + 1,
                    type: type,
                    arguments: '',
                });
                // this.index = this.index + 1;
                // Reset the selected value to default after adding the domain
                this.selectedDomain = '';
            }
        },
        submitForm() {
            //ifvalid()
            //axios
            let temp = []
            temp.push({type: 'Memory',arguments: [parseInt(this.instructionQueue.memory)]})
            temp.push({type: 'Priority',arguments: [this.instructionQueue.priority]})

            this.instructionQueue.domains.forEach(element => {
                let temp1 = []
                let el = element.arguments.split(' ')
                el.forEach(str => {
                    const num = parseInt(str); // 转换为整数
                    if (!isNaN(num)) {
                        temp1.push(num); // 如果转换成功，则添加到args数组中
                    }
                    else
                        temp1.push(str) // 否则就直接添加
                })
                temp.push({type:element.type,arguments:temp1})
            });
            temp.push({type: 'Exit',arguments:[]})
            axios.post(serverURL + '/process', temp)
            
            console.log(temp)
            console.log(this.instructionQueue)
            //reset
        },

    },
}
</script>