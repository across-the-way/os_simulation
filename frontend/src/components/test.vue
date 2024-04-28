<template>
  <el-table-v2
    :columns="columns"
    :data="data"
    :width="700"
    :height="400"
    fixed
  />
</template>

<script lang="ts" setup>
const processQueue = [
  {'name1':[1,2]},
  {'name2':[1,2]},
  {'name3':[3,4,5]},
];

const generateColumns = (length = 10, prefix = 'column-', props?: any) =>
  Array.from({ length }).map((_, columnIndex) => ({
    ...props,
    key: `${prefix}${columnIndex}`,
    dataKey: `${prefix}${columnIndex}`,
    title: columnIndex === 0 ? 'Name' : `Column ${columnIndex - 1}`,
    width: columnIndex === 0 ? 200 : 150, // 给 name 列更多空间
  }));

// 确定需要的列数，加上 name 列
const maxLength = processQueue.reduce((max, item) => {
  const arr = Object.values(item)[0];
  return Math.max(max, arr.length);
}, 0) + 1; // 加 1 为 name 列
const columns = generateColumns(maxLength, 'Queue-', { width: 100 });

// 处理包含 name 的数据
const generateQueueData = (processQueue: Array<{[key: string]: number[]}>, columns: ReturnType<typeof generateColumns>, prefix = 'Queue-') =>
  processQueue.map((item, rowIndex) => {
    const name = Object.keys(item)[0];
    const queue = item[name];
    const rowData = columns.reduce((rowData, column, columnIndex) => {
      if (columnIndex === 0) {
        rowData[column.dataKey] = name; // 设置 name 列的数据
      } else {
        // 因为添加了 name 列，队列数据的索引需要减 1
        rowData[column.dataKey] = queue[columnIndex - 1] !== undefined ? ` ${queue[columnIndex - 1]}` : '';
      }
      return rowData;
    }, { id: `${prefix}${rowIndex}`, parentId: null });
    
    return rowData;
  });

const data = generateQueueData(processQueue, columns);
</script>

