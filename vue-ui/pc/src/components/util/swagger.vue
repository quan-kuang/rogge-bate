<template>
    <div class="swagger">
        <iframe id="swagger" frameborder=0 scrolling=auto :src="url"/>
    </div>
</template>

<script>

export default {
    name: 'swagger',
    data() {
        return {
            url: process.env.VUE_APP_SWAGGER_URL,
        };
    },
    mounted() {
        this.showIframe();
    },
    methods: {
        showIframe() {
            const iframe = document.getElementById('swagger');
            const clientHeight = document.body.clientHeight - 140;
            iframe.style.width = '100%';
            iframe.style.height = clientHeight + 'px';
        },
    },
};
</script>
