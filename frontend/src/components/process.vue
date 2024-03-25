<script setup >
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>
<script>
export default {
    data() {
        return {
            instructions: [] ,
            FormData : ''
        };
    },
    methods: {
        handleSubmit() {
            this.formData.split('\n').forEach( (item) => {
                let tempdir = { type: '', arguments:[]}
                let temp = item.split(' ')
                let temp2 = temp.shift()
                tempdir.type = temp2
                tempdir.arguments = temp
                this.instructions.push(tempdir)
            })
            console.log(this.instructions[0])
            axios.post(serverURL + '/process',
                this.instructions
            )
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
            <!-- <input  required> -->
            <textarea type="text" id="name" v-model="formData" required></textarea>
            <br>
            <button >Submit</button>
        </form>
    </div>
</template>
<style></style>