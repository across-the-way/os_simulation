<script setup>
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'
// import { Folder,DocumentAdd } from '@element-plus/icons-vue'

</script>

<script>
export default {
  data() {
    return {
      fileLocation: '',
      breadcrumbItems: [],
      operation: 'default',
      value: '请选择',
      fileLists: [
        {
          name: 'src',
          type: 'folder',
          imode: '2016-05-03',
        },
        {
          name: 'data.txt',
          type: 'file',
          imode: '2016-05-03',
        },
        
      ],
      city: [
        {
          value: '文件',
          label: '文件',
        },
        {
          value: '文件夹',
          label: '文件夹',
        },
      ]
    }
  },
  created() {
    console.log(window.location.pathname)
    name = window.location.pathname.split('/')
    console.log(name)
    axios.post(serverURL + '/filesystem', { location: window.location.pathname })
      .then(response => {
        // 处理响应结果
        console.log(response.data);
        this.fileLists = response.data
        fileLists1 = fileLists
        fileLists1.forEach(item => {

        })
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
    changeNowStatus(statusvalue) {
      if(this.operation === 'default')
      this.operation = statusvalue
    else this.operation = 'default'
    }

  },
}
</script>
<template>
  <div style="padding: 10rem;padding-bottom: 20px;">
    <div class="fileheader">
      <el-breadcrumb separator="/" class="left-aligned" style="display: inline-flex;margin-left: 8px;">

        <el-breadcrumb-item v-for="path in breadcrumbItems">
          <a :href="path.pathat">{{ path.name }}</a>
        </el-breadcrumb-item>

      </el-breadcrumb>
      <div class="right-aligned" >
        <Transition name="scale">
        <el-button type="success" @click = 'changeNowStatus("add")' v-if="operation === 'default'">
          <el-icon  style="margin-left: 0;margin-right: 5px;" >
            <DocumentAdd />
          </el-icon> add</el-button></Transition>
          <Transition name="scale">
        <el-button type="danger" v-if="operation === 'add'" @click = "changeNowStatus('default')">
          <el-icon style="margin-left: 0;margin-right: 5px;" >
            <CircleClose />
          </el-icon> cancel
        </el-button></Transition>
        <el-select v-model="value" placeholder="Select" style="width: 95px;padding-left: 5px;">
          <el-option v-for="item in city" :key="item.value" :label="item.label" :value="item.value" style="width: 100px;">
            <el-icon><Menu/></el-icon>
            <span style="
          float: right;
          color: var(--el-text-color-secondary);
          font-size: 12px;
        ">{{ item.value }}</span>
          </el-option>
        </el-select>
      </div>

    </div>
    <el-table :data="fileLists" style="width: 100%">
      <el-table-column label="Name" width="180">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <div v-if="scope.row.type === 'folder'">
            <el-icon color="#409efc" style="color: blue;">
              <folder />
            </el-icon>
            <a style="margin-left: 10px" v-bind:href="fileLocation + '/' + scope.row.name">{{ scope.row.name }}</a>
          </div>
        <div v-else>
          <el-icon color="#409efc" style="color: blue;">
              <Document />
            </el-icon>
            <a style="margin-left: 10px;" >{{ scope.row.name }}</a>  
        </div>
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
              <el-tag>{{ scope.row.imode }}</el-tag>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="Operations" width="180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
          <el-button size="small" type="danger" v-if="operation != 'default'" @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.fileheader {

  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
}
.scale-enter-active,
.scale-leave-active {
  transition: transform 0.5s;
}

.scale-enter,
.scale-leave-to {
  transform: scale(0);
}
</style>
