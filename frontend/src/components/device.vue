<script setup>
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
import { dialogEmits } from 'element-plus';

</script>

<script>
export default {
  data() {
    return {
      responseData: null,
      displayList: [],
      dialogTableVisible: false,
      gridData: [
        {
          date: '2016-05-02',
          name: 'John Smith',
          address: 'No.1518,  Jinshajiang Road, Putuo District',
        },
        {
          date: '2016-05-04',
          name: 'John Smith',
          address: 'No.1518,  Jinshajiang Road, Putuo District',
        },
        {
          date: '2016-05-01',
          name: 'John Smith',
          address: 'No.1518,  Jinshajiang Road, Putuo District',
        },
        {
          date: '2016-05-03',
          name: 'John Smith',
          address: 'No.1518,  Jinshajiang Road, Putuo District',
        },],
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
  methods: {
    handleSet(data) {
      this.dialogTableVisible = true
      this.displayList=data
    }
  }
}



</script>

<template>
  <div><el-table :data="responseData" style="width: 100%">
      <el-table-column label="type" width="180">
        <template #default="scope">
          <el-popover effect="light" trigger="hover" placement="top" width="190">
            <template #default>
              <div>{{ scope.row.type }}</div>
              <div>{{ scope.row.priority }}</div>
            </template>
            <template #reference>
              <span>{{ scope.row.type }}</span>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="busy" width="180">
        <template #default="scope">
          <el-tag type="danger" v-if="scope.row.busy == false">
            {{ scope.row.busy }}
          </el-tag>
          <el-tag type="success" v-if="scope.row.busy == true">
            {{ scope.row.busy }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="detail" width="180">
        <template #default="scope">
          <el-button size="small" @click="handleSet(scope.row.waitQueue)">display</el-button>

        </template>
      
      </el-table-column>
    </el-table></div>
    <el-dialog v-model="dialogTableVisible" title="waiting Queue" width="400" align-center>
    <el-table :data="displayList">
      <el-table-column property="pid" label="p_id" />
      <el-table-column property="iotime" label="iotime" />
    </el-table>
  </el-dialog>
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
