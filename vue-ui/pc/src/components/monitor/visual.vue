<template>
    <div class="visual">
        <iframe id="visual" frameborder=0 scrolling=auto :src="url" @load="setDefaultAccount"/>
    </div>
</template>

<script>

export default {
    name: 'visual',
    data() {
        return {
            url: process.env.VUE_APP_SPRING_BOOT_ADMIN_URL,
        };
    },
    mounted() {
        this.showIframe();
    },
    methods: {
        showIframe() {
            const iframe = document.getElementById('visual');
            const clientHeight = document.body.clientHeight - 140;
            iframe.style.width = '100%';
            iframe.style.height = clientHeight + 'px';
        },
        /* 本地调试跨域问题尚未解决*/
        setDefaultAccount() {
            const visual = document.getElementById('visual').contentDocument;
            if (visual) {
                const username = visual.getElementById('username');
                const password = visual.getElementById('password');
                if (username && password) {
                    username.value = 'admin';
                    password.value = 'loyer.';
                }
            }
        },
    },
};
</script>
