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
      ioperation: false,
      value: '请选择',
      form: {
        name: '',
        type: '',
      },
      fileLists: [
        {
          name: 'data.txt',
          type: 1,
          imode: '2016-06-03',
        },
        {
          name: 'drc',
          type: 0,
          imode: '2009-05-03',
        },
        {
          name: 'cata.txt',
          type: 1,
          imode: '2010-04-03',
        },
        {
          name: 'src',
          type: 0,
          imode: '2016-10-03',
        },
        {
          name: 'eata.txt',
          type: 1,
          imode: '2016-08-03',
        },
        {
          name: 'arc',
          type: 0,
          imode: '2019-05-03',
        },
        {
          name: 'data.txt',
          type: 1,
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
      return this.operation === 'delete' ? 'danger' : 'primary';
    },
    buttonText() {
      return this.operation === 'delete' ? 'cancel' : 'option';
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
      
      if (this.operation === 'default')
        this.operation = statusvalue
        if(statusvalue === 'add' && !this.ioperation){
          this.ioperation = true
        }
      else this.operation = 'default'
    },
    sortFileLists() {
      this.fileLists.sort((a, b) => {
        if (a.type === b.type) {
          return a.name.localeCompare(b.name); // 同类型时按name排序
        }
        return a.type === 0 ? -1 : 1; // 将folder类型置前
      });
    },
    sortByDateAndName() {
      this.fileLists.sort((a, b) => {
        // 按类型排序，folder始终在前
        if (a.type !== b.type) {
          return a.type === 0 ? -1 : 1;
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
    handleDelete(index, data) {
      
      axios.post(serverURL + '/terminal',['rm',data.name])
      .then((response)=>{
        console.log(response.data)
      })
      .catch(error => {
          console.log(error)
        })
      this.fileLists.splice(index, 1)
    },
    handleReset(){
      this.operation = 'default'
      this.ioperation = false
    },
    newFile(str, type) {
      const date = new Date();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      let currentDate = `${year}-${month}-${day}`;
      let temp = { name: str, type: type, imode: 1 }
      this.fileLists.push(temp)
      console.log(str)

      if(type == 1){
        axios.post(serverURL + '/terminal', ['touch',str] )
        .then(res => {
          console.log(res.data)
        })
        .catch(error =>{
          console.log(error)
        })
      }
      else {
        axios.post(serverURL + '/terminal', ['mkdir',str] )
        .then(res => {
          console.log(res.data)
        })
        .catch(error =>{
          console.log(error)
        })
      }
      this.fileLists.sort((a, b) => {
        if (a.type == b.type) {
          return a.name.localeCompare(b.name); // 同类型时按name排序
        }
        return a.type == 0 ? -1 : 1; // 将folder类型置前
      });
      // this.sortFileLists()
      this.form.name = ''
      this.form.type = ''
      this.operation = 'default'
      this.ioperation = false
      console.log(this.operation)
    }
  },
}
</script>
<template>
  
  <el-dialog v-model="ioperation" title="apply file" width="500">
    <el-form :model="form">
      <el-form-item label="Promotion name" label-width="140px">
        <el-input v-model="form.name" autocomplete="off" />
      </el-form-item>
      <el-form-item label="Zones" label-width="140px">
        <el-select v-model="form.type" placeholder="Please select a type">
          <el-option label="folder" value=0 />
          <el-option label="file" value=1 />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="ioperation = false">Cancel</el-button>
        <el-button type="primary" @click="newFile(form.name,form.type)">
          Confirm
        </el-button>
      </div>
    </template>
  </el-dialog>
  <div style="padding: 10rem;padding-bottom: 20px;">
  
    <div class="fileheader">
      <el-breadcrumb separator="/" class="left-aligned" style="display: inline-flex;margin-left: 8px;">

        <el-breadcrumb-item v-for="path in breadcrumbItems">
          <a :href="path.pathat">{{ path.name }}</a>
        </el-breadcrumb-item>

      </el-breadcrumb>
      <div class="right-aligned">

        <!-- 按时间排序/取消按钮 -->
        <el-button @click="changeNowStatus('add')" type="success">add files</el-button>
        <el-button :type="!isSortedByDate ? 'primary' : 'info'" @click="toggleSortByDate">
          {{ isSortedByDate ? '取消' : '按时间排序' }} <!-- 根据isSortedByDate显示不同的文本 -->
        </el-button>

        <Transition name="scale">
          <el-button :type="buttonClass" @click='changeNowStatus("delete")'>
            <el-icon style="margin-left: 0;margin-right: 5px;" v-if="operation != 'delete'">
              <DocumentRemove />
            </el-icon>
            <el-icon style="margin-left: 0;margin-right: 5px;" v-if="operation === 'delete'">
              <CircleClose />
            </el-icon>
            {{ buttonText }}</el-button>
        </Transition>

        <el-select v-model="value" placeholder="Select" style="width: 105px;padding-left: 12px;">
          <el-option v-for="item in city" :key="item.value" :label="item.label" :value="item.value"
            style="width: 120px;">
            <el-icon>
              <Menu />
            </el-icon>
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
            <div v-if="scope.row.type == 0">
              <el-icon color="#409efc" style="color: blue;">
                <folder />
              </el-icon>
              <a style="margin-left: 10px" v-bind:href="fileLocation + '/' + scope.row.name">{{ scope.row.name }}</a>
            </div>
            <div v-else>
              <el-icon color="#409efc" style="color: blue;">
                <Document />
              </el-icon>
              <a style="margin-left: 10px;">{{ scope.row.name }}</a>
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
      <el-table-column label="Operations" width="180"v-if="operation == 'delete'">
        <template #default="scope">
          <el-button size="small" v-if="scope.row.type==1" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
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

.add {
  z-index: 10;
  top: 40vh;
  left: calc(50vw - 100px);
  position: absolute;
  width: 25vw;
  border-radius: 30px;
  align-items: center;
  padding: 5vw,5vh;
  height: 20vh;
  background-color: white;
}
.addzhezhao{
  background-color: rgba(0,0,0,0.3);
  z-index: 9;
  width: calc(100vw - 240px);
  height: calc(100vh - 100px);
  position: absolute;
}
</style>
