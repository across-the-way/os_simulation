const routes = [
    {
        path: '/memory',
        name: 'memory',
        title: '内存情况',
        meta:{
            title: '内存情况'
        },
        component: () => import('@/components/memory.vue'),
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
        path: '/test',
        name: 'test',
        title: '测试页',
        meta:{
            title: '测试页'
        },
        component: () => import('@/components/test.vue'),
    },
    {
        path: '/test1',
        name: 'test1',
        title: '测试页',
        meta:{
            title: '测试页1'
        },
        component: () => import('@/components/test1.vue'),
    },
    {
        path: '/process',
        name: 'process',
        title: 'process',
        meta:{
            title: '进程详情'
        },
        component: () => import('@/components/process.vue'),
    },
    {
        path: '/process/queue',
        name: 'processqueue',
        title: 'processqueue',
        meta:{
            title: '进程队列'
        },
        component: () => import('@/components/processQueue.vue'),
    },
    {
        path: '/process/create/gui',
        name: 'processcreategui',
        title: 'sdfasd',
        meta:{
            title: '创建进程(图形化)'
        },
        component: () => import('@/components/processCreateGUI.vue'),
    },
    {
        path: '/device',
        name: 'device',
        title: '设备管理',
        meta:{
            title: '设备管理'
        },
        component: () => import('@/components/device.vue'),
    },
    {
        path: '/terminal',
        name: 'ter',
        title: '终端',
        meta:{
            title: '终端'
        },
        component: () => import('@/components/ter.vue'),
    },
    {
        path: '/process/create/cmd',
        name: 'processcreatecmd',
        title: '进程创建',
        meta:{
            title: '进程创建(命令行)'
        },
        component: () => import('@/components/processCreateCmd.vue'),
    },
]
export default routes