<template>
  <el-button @click="fetchdata3">test2</el-button>
  <div
    style="border-radius: 20px;max-width: 950px;margin: auto;background-color: white;padding: 20px;overflow-wrap: break-word;display: flex;flex-wrap: wrap;">
    <div v-for="item in tmp" style="display: flex;">
      <el-popover placement="top-start" title="Title" :width="200" trigger="hover">
        <template #reference>
          <div class="square" :style="{ 'background-color': item.isactive ? 'red' : 'blue' }">
            pid:{{ item.pid }}<br>
              虚拟页号：{{ item.page_num_virtual }}
          </div>
        </template>
        <Memorydetail :msg="item.page_num_physical"></Memorydetail>
      </el-popover>
    </div>
  </div>
</template>
<script setup>
import axios from "axios"
import { serverURL } from '@/configjs/ServerURL'
import Memorydetail from '@/components/Memorydetail.vue'
</script>
<script>
export default {
  components:{
    Memorydetail
  },
  data() {
    return {
      showData: false,
      tmp: [],
    };
  },
  mounted() {
    this.fetchdata3();
  },
  methods: {
    fetchdata3() {
      this.tmp = []
      axios.get(serverURL + "/api/memory")
        .then(res => {
          if (res.data.strategy === 'LRU' || res.data.strategy === 'FIFO') {
            let page = res.data.details.free_pages
              let temp = page.slice(1, -1).split(',')
              console.log(temp.length)
            if (temp[0]!='') {
              
              
              console.log(temp)
                temp.forEach(page => {
                  this.tmp.push({ page_num_physical: parseInt(page), page_num_virtual: '不存在', pid: '无' ,isactive: false})
                })
            }
            if (res.data.details.lru_cache) {
              const lruCache = Object.entries(res.data.details.lru_cache).map(
                ([key, value]) => ({
                  key,
                  value,
                })
              );
              lruCache.forEach((item) => {
                console.log(item.key, item.value);
                this.tmp.push({page_num_physical: item.value.page_num_physical, page_num_virtual: item.value.page_num_virtual, pid: item.value.pid ,isactive: true});
              });
            }
            console.log(this.tmp)
            // sort(this.tmp)
            this.tmp.sort((a, b) => 
              a.page_num_physical - b.page_num_physical
            );
          }

          console.log(this.tmp)
        })
    }
  },

};
</script>

<style>
.square {
  width: 160px;
  height: 160px;
  margin: 5px;
  padding: 8px;
  border-radius: 6px;
  
  background-color: #ccc;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
</style>