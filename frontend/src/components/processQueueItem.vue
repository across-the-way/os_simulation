<script setup>
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>
<script>
export default {
    data() {
        return {
            instructions: [],//指令队列
            FormData: ''//待处理的指令队列
        };
    },
    methods: {
        handleSubmit() {
            this.formData.split('\n').forEach((item) => {
                let tempdir = { type: '', arguments: [] }
                let temp = item.split(' ')
                let temp2 = temp.shift()
                let temp1 = []
                temp.forEach(str => {
                    const num = parseInt(str); // 转换为整数
                    if (!isNaN(num)) {
                        temp1.push(num); // 如果转换成功，则添加到args数组中
                    }
                    else
                        temp1.push(str) // 否则就直接添加
                })
                tempdir.type = temp2
                tempdir.arguments = temp1
                this.instructions.push(tempdir) //将处理好的指令加入数组中
            })
            console.log(this.instructions)
            axios.post(serverURL + '/process', this.instructions)//发送指令队列
                .then(response => {
                    // 处理响应结果
                    console.log(response.data);
                    this.responseData = response.data
                })
                .catch(error => {
                    // 处理错误
                    console.error(error);
                });
            this.instructions = []
        }

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
    <div>
        <form @submit.prevent="handleSubmit">
            <label for="name">Name:</label>
            <br>
            

            <br>
        </form>
    </div>
</template>
<style></style>