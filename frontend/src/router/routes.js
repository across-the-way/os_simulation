const routes = [
    {
        path: '/',
        name: 'index',
        title: '首页',
        component: () => import('@/components/HelloWorld.vue'),
    },
    {
        path: '/filesystem',
        name: 'filesystem',
        title: '文件系统',
        component: () => import('@/page/filesystem/filesystem.vue'),
    }
]
export default routes