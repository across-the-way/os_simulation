<script setup>
import axios from 'axios'
import { serverURL } from '@/configjs/ServerURL'
import { dialogEmits } from 'element-plus';

</script>

<script>
export default {
  data() {
    return {
      responseData: null,
      displayList: [],
      operation: 'default',
      dialogTableVisible: false,
      ioperation: false,
      name: '',
      mainkey: 1,
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
  computed: {
    buttonClass() {
      return this.operation == 'delete' ? 'danger' : 'primary';
    },
    buttonText() {
      return this.operation == 'delete' ? 'cancel' : 'option';
    }
  },
  methods: {
    newdevice(name){
      console.log(name)
      axios.post(serverURL+'/device/add',name)
      .then(res=>{
        console.log(res.data)
      })
      .catch(err=>{
        console.log(err)
      })
      this.ioperation = false
      this.name = ''
      this.mainkey++
    },
    handleSet(data) {
      this.dialogTableVisible = true
      this.displayList = data
    },
    changeNowStatus(statusvalue) {
      console.log(statusvalue);
      if (this.operation === 'default')
        {this.operation = statusvalue
        console.log(this.operation);
        if(statusvalue === 'add' && !this.ioperation){
          this.ioperation = true
        }}
      else 
        {this.operation = 'default'
        console.log('default');}
    },
  }
}



</script>

<template>
  <div class="deviceheader">
      <div class="left-aligned"></div>
      <div class="right-aligned">

        <!-- 按时间排序/取消按钮 -->
        <el-button @click="changeNowStatus('add')" type="success">
          <el-icon style="margin-left: 0;margin-right: 5px;" >
              <DocumentAdd />
            </el-icon>
          add device</el-button>
        

        <Transition name="scale">
          <el-button :type="buttonClass" @click='changeNowStatus("delete")'>
            <el-icon style="margin-left: 0;margin-right: 5px;" v-if="operation != 'delete'">
              <setting />
            </el-icon>
            <el-icon style="margin-left: 0;margin-right: 5px;" v-if="operation == 'delete'">
              <CircleClose />
            </el-icon>
            {{ buttonText }}</el-button>
        </Transition>
      </div>

    </div>
  <div><el-table :data="responseData" style="width: 100%" :key="mainkey">
      <el-table-column label="device_id" width="180">
        <template #default="scope">
          
            <span>{{ scope.row.device_id }}</span>
          
        </template>
      </el-table-column>
      <el-table-column label="type" width="180">
        <template #default="scope">
            <span>{{ scope.row.type }}</span>
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
      <el-table-column label="Operations" width="180"v-if="operation == 'delete'">
        <template #default="scope">
          <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table></div>
    <el-dialog v-model="ioperation" title="apply device" width="500" align-center>
    <el-form :model="form">
      <el-form-item label="device name" label-width="140px">
        <el-input v-model="name" autocomplete="off" />
      </el-form-item>
      
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="ioperation = false">Cancel</el-button>
        <el-button type="primary" @click="newdevice(name)">
          Confirm
        </el-button>
      </div>
    </template>
  </el-dialog>
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
  .deviceheader {

display: flex;
justify-content: space-between;
padding-bottom: 10px;
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
