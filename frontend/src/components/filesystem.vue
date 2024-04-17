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
          name: 'data.txt',
          type: 'file',
          imode: '2016-06-03',
        },
        {
          name: 'drc',
          type: 'folder',
          imode: '2009-05-03',
        },
        {
          name: 'cata.txt',
          type: 'file',
          imode: '2010-04-03',
        },
        {
          name: 'src',
          type: 'folder',
          imode: '2016-10-03',
        },
        {
          name: 'eata.txt',
          type: 'file',
          imode: '2016-08-03',
        },
        {
          name: 'arc',
          type: 'folder',
          imode: '2019-05-03',
        },
        {
          name: 'data.txt',
          type: 'file',
          imode: '2012-04-03',
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
      ],
      isSortedByDate: false,  //跟踪是否按时间排序
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
        this.sortFileLists();
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
  computed: {
    buttonClass() {
      return this.operation === 'default' ? 'success' : 'danger';
    },
    buttonText() {
      return this.operation === 'default' ? 'add' : 'cancel';
    }
  },
  mounted() {
    this.fileLocation = window.location.pathname;
    console.log(window.location.pathname)
    this.generateBreadcrumb();
    console.log(this.breadcrumbItems);
    this.sortFileLists();
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
    },
    sortFileLists() {
      this.fileLists.sort((a, b) => {
        if (a.type === b.type) {
          return a.name.localeCompare(b.name); // 同类型时按name排序
        }
        return a.type === 'folder' ? -1 : 1; // 将folder类型置前
      });
    },
    sortByDateAndName() {
      this.fileLists.sort((a, b) => {
        // 按类型排序，folder始终在前
        if (a.type !== b.type) {
          return a.type === 'folder' ? -1 : 1;
        }
        // 相同类型按日期排序，如果日期相同则按名称排序
        const dateComparison = b.imode.localeCompare(a.imode);
        if (dateComparison !== 0) {
          return dateComparison;
        }
        return a.name.localeCompare(b.name);
      });
    },
    toggleSortByDate() {
      this.isSortedByDate = !this.isSortedByDate;  // 切换排序状态
      if (this.isSortedByDate) {
        this.sortByDateAndName();  // 如果激活按时间排序，则调用按时间排序的方法
      } else {
        this.sortFileLists();  // 否则，使用默认的排序方式
      }
    },
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
        
          <!-- 按时间排序/取消按钮 -->
          <el-button type="info" @click="toggleSortByDate" >
          {{ isSortedByDate ? '取消' : '按时间排序' }} <!-- 根据isSortedByDate显示不同的文本 -->
          </el-button>
        
        <Transition name="scale">
        <el-button :type="buttonClass" @click = 'changeNowStatus("add")' >
          <el-icon  style="margin-left: 0;margin-right: 5px;" v-if="operation === 'default'">
            <DocumentAdd />
          </el-icon>
          <el-icon  style="margin-left: 0;margin-right: 5px;" v-if="operation === 'add'">
            <CircleClose />
          </el-icon>
          {{ buttonText }}</el-button></Transition>
          
        <el-select v-model="value" placeholder="Select" style="width: 105px;padding-left: 12px;">
          <el-option v-for="item in city" :key="item.value" :label="item.label" :value="item.value" style="width: 120px;">
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
