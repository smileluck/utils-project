[toc]
---

# 前言

这是一个字典下拉选择工具，通过switch获取对应的list后，返回关键字信息，让前端能够下拉选择
。

# 使用

## vue3+element-plus

> CustomSelect

1. 将CustomSelect放入 components 文件夹下
2. 在需要使用组件的地方，引入组件。

```vue
import CustomSelect from "@/components/CustomSelect";

// use
<custom-select v-model="queryParams.userId" :clearable="true" placeholder="请输入用户ID" />
```

3. 前后端配置searchType，案例里面支持了 user， 用来查询用户
4. 数据格式请看 SelectVO

```json
{
  "list": [],
  "valueKey": "id",
  "labelKey": "name"
}
```