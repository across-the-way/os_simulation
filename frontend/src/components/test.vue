<template>
    <div>
      <breadcrumb :items="breadcrumbItems"></breadcrumb>
    </div>
  </template>
  
  <script>
  import Breadcrumb from './Breadcrumb.vue';
  
  export default {
    components: {
      Breadcrumb
    },
    computed: {
      breadcrumbItems() {
        const pathSegments = window.location.pathname.split('/').filter(segment => segment !== '');
        const breadcrumbItems = [];
  
        // 添加首页
        breadcrumbItems.push({ text: '首页', link: '/' });
  
        // 逐级添加路径项
        let accumulatedPath = '';
        for (let i = 0; i < pathSegments.length; i++) {
          accumulatedPath += '/' + pathSegments[i];
          breadcrumbItems.push({ text: pathSegments[i], link: accumulatedPath });
        }
  
        return breadcrumbItems;
      }
    }
  };
  </script>