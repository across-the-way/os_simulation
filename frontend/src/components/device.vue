<script setup>
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>

<script>
export default {
  data() {
    return {
      responseData: null,
    }
  },
  created() {
    axios.get(serverURL + '/device', {})
      .then(response => {
        // 处理响应结果
        console.log(response.data);
        this.responseData = response.data
      })
      .catch(error => {
        // 处理错误
        console.error(error);
      });
  },
  methods:{
    handleDelete(index, data) {
      
    }
  }
}



</script>

<template>
   <div><el-table :data="pcb" style="width: 100%">
      <el-table-column label="p_id" width="180">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            {{ scope.row.p_id }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="prority" width="180">
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
      <el-table-column label="detail" width="180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table></div>
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
