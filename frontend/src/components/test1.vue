<template>
  <div>
    <el-table :data="tableData" border>
      <el-table-column v-for="(row, rowIndex) in tableData" :key="rowIndex" :label="`Row`">
        <template slot-scope="scope">
          <el-table-column v-for="(cell, cellIndex) in row" :key="cellIndex" :label="`Column`">
            <template slot-scope="scope">
              {{ cell }}
            </template>
          </el-table-column>
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
        ['A1', 'B1', 'C1'],
        ['A2', 'B2', 'C2'],
        ['A3', 'B3', 'C3']
      ]
    };
  }
};
</script>