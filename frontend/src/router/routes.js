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
        path: '/filesystem',
        name: 'filesystem',
        title: '文件系统',
        meta:{
            title: '文件系统'
        },
        component: () => import('@/components/filesystem.vue'),
    },
    {
        path: '/device',
        name: 'device',
        title: '首页',
        component: () => import('@/components/device.vue'),
    },
]
export default routes