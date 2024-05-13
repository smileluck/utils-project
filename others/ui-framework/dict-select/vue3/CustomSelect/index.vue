<template>
    <div class="el-tree-select">
        <el-select-v2 ref="customSelect" style="width: 100%" v-model="valueId" :filterable="true"
            :clearable="props.clearable" :placeholder="placeholder" @clear="clearHandle" remote
            :remote-method="remoteMethod" :options="options" :loading="loading" :props="selectProps"
            @change="handleChange" />
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { listSelect } from "@/api/tap/common";

const loading = ref(false)

const props = defineProps({
    /**当前双向数据绑定的值 */
    value: {
        type: String,
        default: ''
    },
    clearable: {
        type: Boolean,
        default: false
    },
    /**当前的数据 */
    options: {
        type: Array,
        default: () => []
    },
    /**输入框内部的文字 */
    placeholder: {
        type: String,
        default: ''
    },
    searchType: {
        type: String,
        default: "user",
    }
})

const emit = defineEmits(['update:modelValue']);

const valueId = computed({
    get: () => props.value,
    set: (val) => {
        emit('update:modelValue', val)
    }
});

const selectProps = ref({
    label: 'name',
    value: 'id'
})
const options = ref([])

const remoteMethod = (query) => {
    if (query !== '') {
        loading.value = true
        listSelect({ type: props.searchType, name: query }).then((response) => {
            loading.value = false
            if (response.data != null) {
                options.value = response.data.list;
                selectProps.value = {
                    label: response.data.labelKey,
                    value: response.data.valueKey
                }
            } else {
                options.value = []
            }
        }).catch((e) => {
            loading.value = false;
        })
    } else {
        options.value = []
    }
}

const handleChange = (val) => {
    valueId.value = val;
}

function initHandle() {
    nextTick(() => {
        const selectedValue = valueId.value;
        if (selectedValue !== null && typeof (selectedValue) !== 'undefined') {

        } else {
            clearHandle()
        }
    })
}

function clearHandle() {
    valueId.value = ''
}

onMounted(() => {
    initHandle()
})

watch(valueId, () => {
    initHandle();
})
</script>