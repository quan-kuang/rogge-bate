<template>
    <div class="generateForm">
        <iframe id="generateForm" frameborder=0 scrolling=auto :src="url"/>
    </div>
</template>

<script>
export default {
    name: 'generateForm',
    data() {
        return {
            url: 'https://mrhj.gitee.io/form-generator/#/',
        };
    },
    mounted() {
        this.showIframe();
    },
    methods: {
        showIframe() {
            const iframe = document.getElementById('generateForm');
            const clientHeight = document.body.clientHeight - 140;
            iframe.style.width = '100%';
            iframe.style.height = clientHeight + 'px';
        },
    },
};
</script>
