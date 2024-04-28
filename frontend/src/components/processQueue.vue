<script setup>
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>

<script>
export default {
    data() {
        return {
            responseData:

            {Waiting_Queues: [[1,2],[1,2]],
            Ready_Queue: [1,2],
            Second_Queue: [1,11,21],
            Swapped_Ready_Queue: [],
            Swapped_Waiting_Queue: [],}

        }
    },
    created() {
        axios.get(serverURL + '/process/queue')
            .then(response => {
                // 处理响应结果
                console.log(response.data);
                // this.responseData = response.data;

            })
            .catch(error => {
                // 处理错误
                console.error(error);
            });
    }
}



</script>

<template>
    <div><el-table :row-key="responseData.Waiting_Queues" style="width: 100%">
        <!-- <el-table-column label="type" width="180">
                <template #default="scope">
                    <span>{{ item }}</span>
                </template>
            </el-table-column> -->
        <el-table-column label="type" width="180" >
                <template #default="scope">
                    <el-table-column label="type" width="180" :prop="item" v-for="(item,i) in scope.row">
                        <span>{{ scope.row[i] }}</span>
                    </el-table-column>
                </template>
            </el-table-column>
            
        </el-table>
        <el-table :data="responseData.Ready_Queue" style="width: 100%">
            <el-table-column label="type" width="180" v-for="(item,i) in responseData.Ready_Queue">
                <template #default="scope">
                    {{ scope.row[i] }}
                </template>
            </el-table-column>
            <!-- <el-table-column label="resourcetype" width="180">
                <template #default="scope">
                    <span>{{ scope.row }}</span>
                </template>
            </el-table-column> -->
        </el-table>
        <el-table :data="responseData.Second_Queue" style="width: 100%">
            <el-table-column label="type" width="180">
                <template #default="scope">
                    <span>{{ scope.row }}</span>
                </template>
            </el-table-column>
            <el-table-column label="resourcetype" width="180">
                <template #default="scope">
                    <span>{{ scope.row }}</span>
                </template>
            </el-table-column>
        </el-table>
        <el-table :data="Swapped_Ready_Queue" style="width: 100%">
            <el-table-column label="type" width="180">
                <template #default="scope">
                    <span>{{ scope.row }}</span>
                </template>
            </el-table-column>
            <el-table-column label="resourcetype" width="180">
                <template #default="scope">
                    <span>{{ scope.row }}</span>
                </template>
            </el-table-column>
        </el-table>
        <el-table :data="responseData.Swapped_Waiting_Queue" style="width: 100%">
            <el-table-column label="type" width="180">
                <template #default="scope">
                    <span>{{ scope.row }}</span>
                </template>
            </el-table-column>
            <el-table-column label="resourcetype" width="180">
                <template #default="scope">
                    <span>{{ scope.row }}</span>
                </template>
            </el-table-column>


        </el-table>
    </div>
</template>

<style scoped>
header {
    line-height: 1.5;
}

.logo {
    display: block;
    margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
    header {
        display: flex;
        place-items: center;
        padding-right: calc(var(--section-gap) / 2);
    }

    .logo {
        margin: 0 2rem 0 0;
    }

    header .wrapper {
        display: flex;
        place-items: flex-start;
        flex-wrap: wrap;
    }
}
</style>
