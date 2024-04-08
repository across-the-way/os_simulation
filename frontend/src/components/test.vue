<template>
  <div>
    <el-table :data="tableData">
      <el-table-column label="Name">
        <template #default="scope">
          {{ scope.row.name }}
        </template>
      </el-table-column>
      <el-table-column label="Age">
        <template #default="scope">
          {{ scope.row.age }}
        </template>
      </el-table-column>
      <el-table-column label="Country">
        <template #default="scope">
          {{ scope.row.country }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [
        { name: 'John', age: 25, country: 'USA' },
        { name: 'Alice', age: 30, country: 'Canada' },
        { name: 'Bob', age: 28, country: 'UK' }
      ]
    };
  }
};
</script>