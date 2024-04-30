<script setup>
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>

<script>
export default {
  data() {
    return {
      responseData: null,
      form: {
        LongTerm_CeilThreshold: 0,
        LongTerm_FloorThreshold: '',
        MidTermScale: '',
        MidTerm_CeilThreshold: '',
        MidTerm_FloorThreshold: false,
        Memory_Size: [],
        Page_Size: '',
        Printer_Number: '',
        Printer_Number: '',
        Test_Max_Line: '',
        Test_Max_Time: '',
        availableMap: [
          {string: '', value:0},
        ],
        CPUstrategy: null,
        MMstrategy: null,
      }
    }
  },
  created() {
    // axios.get(serverURL + '/', {})
    //   .then(response => {
    //     // 处理响应结果
    //     console.log(response.data);
    //     this.responseData = response.data
    //   })
    //   .catch(error => {
    //     // 处理错误
    //     console.error(error);
    //   });
  },
  methods:{
    handSubmit(){
      axios.post(serverURL + '/',form)
      .then(res => {
        console.log(res.data);

      })
      .catch(error => {
        console.log(error);
      });
      localStorage.setItem('config',JSON.stringify(this.form))
    },
    ondelete(){
      localStorage.clear()
    }
  }
}



</script>

<template>
  <el-form :model="form" label-width="auto" style="max-width: 600px">
    <el-form-item label="Activity name">
      <el-input v-model="form.SystemPulse" />
    </el-form-item>
    <el-form-item label="Activity zone">
      <el-select v-model="form.InstructionLength" placeholder="please select your zone">
        <el-option label="Zone one" value="shanghai" />
        <el-option label="Zone two" value="beijing" />
      </el-select>
    </el-form-item>
    <el-form-item label="Activity time">
      <el-col :span="11">
        <el-date-picker v-model="form.date1" type="date" placeholder="Pick a date" style="width: 100%" />
      </el-col>
      <el-col :span="2" class="text-center">
        <span class="text-gray-500">-</span>
      </el-col>
      <el-col :span="11">
        <el-time-picker v-model="form.date2" placeholder="Pick a time" style="width: 100%" />
      </el-col>
    </el-form-item>
    <el-form-item label="Instant delivery">
      <el-switch v-model="form.delivery" />
    </el-form-item>
    <el-form-item label="Activity type">
      <el-checkbox-group v-model="form.type">
        <el-checkbox value="Online activities" name="type">
          Online activities
        </el-checkbox>
        <el-checkbox value="Promotion activities" name="type">
          Promotion activities
        </el-checkbox>
        <el-checkbox value="Offline activities" name="type">
          Offline activities
        </el-checkbox>
        <el-checkbox value="Simple brand exposure" name="type">
          Simple brand exposure
        </el-checkbox>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item label="Resources">
      <el-radio-group v-model="form.resource">
        <el-radio value="Sponsor">Sponsor</el-radio>
        <el-radio value="Venue">Venue</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="Activity form">
      <el-input v-model="form.desc" type="textarea" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">Create</el-button>
      <el-button>Cancel</el-button>
    </el-form-item>
  </el-form>
  <el-button type="primary" @click="ondelete">Create</el-button>
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
