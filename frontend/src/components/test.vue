<template>
  <div>
    <ul>
      <li v-for="item in mappedItems" :key="item.key">
        {{ item.key }} - {{ item.value }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      mapper: {
        1: {name:'value1'},
        2: 'value2',
        3: 'value3'
      }
    }
  },
  computed: {
    mappedItems() {
      return Object.entries(this.mapper).map(([key, value]) => ({
        key,
        value
      }));
    }
  }
};
</script>