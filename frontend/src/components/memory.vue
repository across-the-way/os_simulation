<template>
  <div style="height: 60vh; width: 60vw; margin: auto; align-items: center">
    <Doughnut :data="data" :options="options" :key="componentkey" />
  </div>
  <el-dialog v-model="detail" title="详情" width="500" align-center>
    <!-- {{tmp}} -->
    <el-table :data="tmp">
      <el-table-column property="page_num_physical" label="物理页号" />
      <el-table-column property="pid" label="p_id" />
      <el-table-column property="page_num_virtual" label="虚拟页号" />
    </el-table>
   <!-- {{ details.lru_cache}} -->
  </el-dialog>
  <el-descriptions title="页式分配" style="
      max-width: 800px;
      margin: auto;
      background-color: white;
      padding: 20px;
      margin-top: 20px;
    " v-if="ioperation == 2 && strategy != 'Page'">
    <el-descriptions-item label="详情">
      <el-button @click="showdetail">展示</el-button>
      <!-- {{ visualpage }} -->
    </el-descriptions-item>
    
    <el-descriptions-item label="Strategy">
      {{ strategy }}
    </el-descriptions-item>
    <el-descriptions-item label="Faults">
      {{ details.faults/details.pages }}
    </el-descriptions-item>
    <el-descriptions-item label="交换页数">
      {{ details.swapped_page_count }}
    </el-descriptions-item>
    <el-descriptions-item label="使用页数">
      {{ details.swapped_used_count }}
    </el-descriptions-item>
  </el-descriptions>
  <el-descriptions title="分配方式" style="
      max-width: 800px;
      margin: auto;
      background-color: white;
      padding: 20px;
      margin-top: 20px;
    " v-else-if="strategy == 'Page'">
    <!-- <el-descriptions-item label="Username1">{{ this.details.used_pages }}</el-descriptions-item> -->
    <el-descriptions-item label="Strategy">
      {{ strategy }}
    </el-descriptions-item>
  </el-descriptions>
  <el-descriptions title="连续分配" style="
      max-width: 800px;
      margin: auto;
      background-color: white;
      padding: 20px;
      margin-top: 20px;
    " v-else>
    <el-descriptions-item label="Strategy">
      {{ strategy }}
    </el-descriptions-item>
  </el-descriptions>
</template>

<script setup>
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut, Pie } from "vue-chartjs";
import axios from "axios";
import { serverURL } from "@/configjs/ServerURL";
ChartJS.register(ArcElement, Tooltip, Legend);
</script>

<script>
export default {
  // components: {
  //   Pie
  // },
  data() {
    return {
      componentkey: 0,
      timer: null,
      data: {
        labels: [],
        datasets: [
          {
            backgroundColor: [
              "#41B883",
              "#E46651",
              "#00D8FF",
              "#DD1B16",
              "#6a3f9b",
            ],
            data: [],
          },
        ],
      },
      strategy: "",
      page: false,
      memory: {
        strategy: "",
        free_blocks: [
          {
            start: 2200,
            end: 4095,
            size: 1896,
          },
        ],
        used_memory: [],
      },
      details: {
        faults: 0,
        // strategy: '',
        free_pages: {
          empty: false,
        },
        lru_cache: [
          {
            page_num_physical: 2,
            pid: 0,
            page_num_virtual: 0,
          },
        ],
        pages: 0,
        swaped_page_count: 0,
        swaped_used_count: 0,
        used_pages: [],
      },
      visualpage: [],
      continueallocate: ["FirstFit", "NextFit", "BestFit", "WorstFit"],
      pageallocate: ["Page", "LRU", "FIFO"],
      options: {
        responsive: true,
        maintainAspectRatio: false,
      },
      temp: null,
      ioperation: 0,
      tmp: [],
      detail: false,
    };
  },
  created() {
    // this.fetchdata();
    // console.log(this.temp)
    // this.paintcon()
    // .then()
  },
  mounted() {
    this.fetchdata()
  },
  beforeUnmount() {
    this.stopfetchData()
  },

  methods: {
    showdetail(){
      this.detail = true
    },
    stopfetchData() {
      clearInterval(this.timer);
    },
    fetchdata() {
      this.timer = setInterval(() => {
        this.data.labels = []
        this.data.datasets[0].data = []
        axios.get(serverURL + "/memory").then((response) => {
          console.log(response.data);
          if (this.continueallocate.includes(response.data.strategy)) {
            this.ioperation = 1;
            console.log(response.data,'test');
            this.memory = response.data.details;
            this.strategy = response.data.strategy;
            this.temp = Object.entries(this.memory.used_memory).map(
              ([key, value]) => ({
                key,
                value,
              })
            );
            // this.temp = []
            let temp = [];
            this.memory.free_blocks.forEach((block) => {
              this.temp.push({ key: "free", value: block });
            });
            this.temp.sort((a, b) => a.value.start - b.value.start);
            this.temp.forEach((item) => {
              temp.push(item.value.size);
              let data = item.key;
              if (item.key === "free") {
                this.data.labels.push(data);
              } else this.data.labels.push("pid-" + data);
            });
            // console.log(temp);
            this.data.datasets[0].data = temp;

            this.componentkey++;
            //将数据处理好塞到data中
          }
          else if(response.data.strategy == 'Page'){
            console.log(response.data.details)
            this.details = response.data.details
            this.strategy = 'Page'
            let temp = []
            let sum = 0
            const map = new Map()
            this.data.labels = []

            const up = Object.entries(this.details.used_pages).map(
              ([key, value]) => ({
                key,
                value,
              })
            );
            up.forEach(item => {
              temp.push(item.value.size);
              this.data.labels.push('pid-'+ item.key)
              sum += item.value.size
            })
            temp.push(4096-sum)
            this.data.datasets[0].data = temp
            this.data.labels.push('free')
            // console.log(temp)
            this.componentkey++
          } 
          else {
            this.ioperation = 2;
            this.details = response.data.details;
            this.strategy = response.data.strategy;
            let temp = [];
            const lruCache = Object.entries(this.details.lru_cache).map(
              ([key, value]) => ({
                key,
                value,
              })
            );
            let sum = 0;
            let addressMap = new Map();
            let virtualPageMap = new Map();
            let lastPid = null; //////////////////////////////
            lruCache.forEach(item => {
              // console.log(item.value)
              this.tmp.push(item.value);
            })
            lruCache.forEach((item) => {
              //处理物理地址和大小
              const physicalAddress = item.value.page_num_physical * 8; // 乘以8得到物理地址
              let newSegment = true;

              if (item.value.pid === lastPid) {
                let segments = addressMap.get(item.value.pid);
                let lastSegment = segments[segments.length - 1];
                if (lastSegment.end + 1 === physicalAddress) {
                  // 如果地址连续，更新最后一段
                  lastSegment.end = physicalAddress + 7;
                  lastSegment.size = lastSegment.end - lastSegment.start + 1;
                  newSegment = false;
                }
              }

              if (newSegment || lastPid !== item.value.pid) {
                // 如果pid变了或者需要新段
                let segment = {
                  start: physicalAddress,
                  end: physicalAddress + 7,
                  size: 8, // 默认大小为8
                };
                if (addressMap.has(item.value.pid)) {
                  addressMap.get(item.value.pid).push(segment);
                } else {
                  addressMap.set(item.value.pid, [segment]);
                }
              }

              lastPid = item.value.pid; // 更新lastPid为当前处理的pid

              // 收集虚拟页面号
              if (virtualPageMap.has(item.value.pid)) {
                let pages = virtualPageMap.get(item.value.pid);
                if (!pages.includes(item.value.page_num_virtual)) {
                  pages.push(item.value.page_num_virtual);
                }
                virtualPageMap.set(item.value.pid, pages);
              } else {
                virtualPageMap.set(item.value.pid, [item.value.page_num_virtual]);
              }
            });

            // 更新this.temp和this.visualpage
            this.temp = Array.from(addressMap, ([pid, value]) => ({
              key: `pid-${pid}`,
              value: value,
            }));
            this.visualpage = Array.from(virtualPageMap, ([pid, pages]) => ({
              pid: pid,
              pages: pages,
            }));

            // console.log(this.temp, "aa");

            //处理temp
            this.temp = Array.from(addressMap, ([pid, segments]) => {
              return segments.map((segment) => ({
                ...segment,
                pid: pid, // 保留pid信息
              }));
            }).flat(); // 将所有pid的数组扁平化成一个数组
            // 根据start地址排序
            this.temp.sort((a, b) => a.start - b.start);

            // 输出地址和大小的计算结果
            console.log(this.visualpage); // 输出虚拟页面号的结果

            this.temp.forEach((segment) => {
              sum += segment.size;
              this.data.labels.push(`pid-${segment.pid}`);
              temp.push(segment.size);
            });
            // console.log(temp);
            temp.push(4096 - sum);
            this.data.labels.push("free");
            
            this.data.datasets[0].data = temp;
            // console.log(this.data, "a");
            this.componentkey++;
            //将数据处理好塞入data中
          }
        });
      }, 5000)

    },
  },
};
</script>