<template>
  <div class="container">
    <div class="select-box" @click="toggleOptions">
      {{ selectedOption }}
      <ul v-if="showOptions" class="options">
        <li v-for="option in options" :key="option" @click="selectOption(option)">
          {{ option }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      options: ['Option 1', 'Option 2', 'Option 3'], // 选项列表
      showOptions: false, // 是否显示选项列表
      selectedOption: 'Select an option' // 当前选中的选项
    };
  },
  methods: {
    toggleOptions() {
      this.showOptions = !this.showOptions;
    },
    selectOption(option) {
      this.selectedOption = option;
      this.showOptions = false;
      this.handleOptionSelection(option); // 触发自定义函数
    },
    handleOptionSelection(option) {
      // 在这里处理选项选择后的逻辑
      console.log('选中的选项:', option);
    }
  }
};
</script>

<style>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.select-box {
  position: relative;
  display: inline-block;
  padding: 8px;
  border: 1px solid #ccc;
  cursor: pointer;
}

.options {
  position: absolute;
  top: 100%;
  left: 0;
  margin: 0;
  padding: 0;
  list-style: none;
  background-color: #fff;
  border: 1px solid #ccc;
  border-top: none;
}

.options li {
  padding: 8px;
  cursor: pointer;
}

.options li:hover {
  background-color: #f0f0f0;
}
</style>