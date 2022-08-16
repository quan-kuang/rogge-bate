<template>
    <div>
        示例一：demoTask.haveParams('String',10I,10i,1000L,1000l,1.3F,1.3f,10.88D,10.88d,trueB,falseb)<br/>
        备注：demoTask为beanName，haveParams为方法名<br/>
        示例二：com.loyer.common.quartz.task.DemoTask.noParams<br/>
        备注：前者为完整的class名称，noParams为方法名<br/>
        参数类型：支持String和int,float,double,doubl,boolean及其包装器类型<br/>
        注意：根据大小写区分包装器类型和基本数据类型
    </div>
</template>

<script>
export default {
    name: 'hint',
};
</script>
