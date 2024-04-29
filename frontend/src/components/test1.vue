<template>
  <div>
    <h1>信息存储与查看</h1>

    <label for="info">信息：</label>
    <input type="text" id="info" v-model="inputInfo" placeholder="输入要存储的信息">
    <button @click="storeInfo">存储</button>

    <h2>存储的信息：</h2>
    <ul>
      <li v-for="info in storedInfo" :key="info">{{ info }}</li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      inputInfo: '',
      storedInfo: []
    };
  },
  mounted() {
    this.displayStoredInfo();
  },
  methods: {
    storeInfo() {
      this.storedInfo.push(this.inputInfo);
      localStorage.setItem('storedInfo', JSON.stringify(this.storedInfo));
      this.inputInfo = '';
    },
    displayStoredInfo() {
      const storedInfo = JSON.parse(localStorage.getItem('storedInfo')) || [];
      this.storedInfo = storedInfo;
    }
  }
};
</script>