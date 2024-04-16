<script setup >
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
import { Folder } from '@element-plus/icons-vue'

</script>

<script >
export default {
  data() {
    return {
      fileLocation: '',
      breadcrumbItems: [],
      fileLists: [
        {
        name: '2016-05-03',
        type: 'Tom',
        imode: 'No. 189, Grove St, Los Angeles',
      },
      // {
      //   name: '2016-05-03',
      //   type: 'Tom',
      //   imode: 'No. 189, Grove St, Los Angeles',
      // },
      // {
      //   name: '2016-05-03',
      //   type: 'Tom',
      //   imode: 'No. 189, Grove St, Los Angeles',
      // },
      // {
      //   name: '2016-05-03',
      //   type: 'Tom',
      //   imode: 'No. 189, Grove St, Los Angeles',
      // },
    ]
    }
  },
  created() {
    console.log(window.location.pathname)
    name = window.location.pathname.split('/')
    console.log(name)
    axios.post(serverURL + '/filesystem',{location: window.location.pathname} )
      .then(response => {
        // 处理响应结果
        console.log(response.data);
        this.fileLists = response.data
        // this.responseData = response.data
      })
      .catch(error => {
        // 处理错误
        console.error(error);
      });
    // console.log(window.location.pathname)
  },
  mounted() {
    this.fileLocation = window.location.pathname;
    console.log(window.location.pathname)
    this.generateBreadcrumb();
    console.log(this.breadcrumbItems);
  
  },
  methods: {
    generateBreadcrumb() {
      const pathArray = window.location.pathname.split('/'); // 根据当前路径生成路径数组
      pathArray.shift();
      let currentPath = '/';
      pathArray.forEach((path, index) => {
        currentPath += path;
        const breadcrumbItem = {
          pathat: currentPath,
          name: path
        }
        this.breadcrumbItems.push(breadcrumbItem);
        currentPath += '/';
      });
    },

  },
}



</script>

<template>
  <div style="padding: 10rem;padding-bottom: 20px;">
    
    <el-breadcrumb separator="/" style="padding-bottom: 10px;">
     
      <el-breadcrumb-item v-for="path in breadcrumbItems">
      <a :href="path.pathat">{{ path.name }}</a>
    </el-breadcrumb-item>

    </el-breadcrumb>
    <el-table :data="fileLists" style="width: 100%">
      <el-table-column label="Name" width="180">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <el-icon color="#409efc" style="color: blue;">
              <folder />
            </el-icon>
            <a style="margin-left: 10px" v-bind:href="fileLocation + '/' + scope.row.name " >{{ scope.row.name }}</a>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Date" width="180">
        <template #default="scope">
          <el-popover effect="light" trigger="hover" placement="top" width="190">
            <template #default>
              <div>{{ scope.row.type }}</div>
              <div>{{ scope.row.imode }}</div>
            </template>
            <template #reference>
              <el-tag>{{ scope.row.type }}</el-tag>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="Operations" width="180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped></style>
