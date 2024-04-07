const routes = [
    {
        path: '/cpu',
        name: 'index',
        title: '首页',
        meta:{
            title: '中央处理器'
        },
        component: () => import('@/components/cpu.vue'),
    },
    {
        path: '/filesystem/:id*',
        name: 'filesystem',
        title: '文件系统',
        meta:{
            title: '文件系统'
        },
        component: () => import('@/components/filesystem.vue'),
    },

    {
        path: '/process/queue',
        name: 'processqueue',
        title: '文件系统',
        meta:{
            title: '进程队列'
        },
        component: () => import('@/components/processQueue.vue'),
    },
    {
        path: '/device',
        name: 'device',
        title: '首页',
        meta:{
            title: '首页'
        },
        component: () => import('@/components/device.vue'),
    },
    {
        path: '/process',
        name: 'process',
        title: '进程',
        meta:{
            title: '进程'
        },
        component: () => import('@/components/process.vue'),
    },
]
export default routes