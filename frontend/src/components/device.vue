<script setup>
import HelloWorld from '@/components/HelloWorld.vue'
import TheWelcome from '@/components/TheWelcome.vue'
import axios from 'axios'
import { serverURL } from '@/components/ServerURL'

</script>

<script>
export default {
  data() {
    return {
      breadcrumbItems: [] // 存储面包屑项的数组
    };
  },
  mounted() {
    this.generateBreadcrumb();
    console.log(this.breadcrumbItems)
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
  // watch: {
  //   $route() {
  //     this.breadcrumbItems = []; // 清空面包屑项数组
  //     this.generateBreadcrumb(); // 重新生成面包屑
  //   }
  // }
};
</script>

<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <!-- <el-breadcrumb-item :to="{ path: '/device' }">首页</el-breadcrumb-item> -->
      <el-breadcrumb-item v-for="path in breadcrumbItems">
      <a :href="path.pathat">{{ path.name }}</a>
    </el-breadcrumb-item>

    </el-breadcrumb>
  </div>
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
