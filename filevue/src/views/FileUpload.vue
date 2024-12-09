<template>
  <div>

    <el-form ref="formRef" class="full-screen-form" :model="formData" label-width="100px">
      <el-form-item label="选择数据库">
        <el-radio-group v-model="formData.database">
          <el-radio :value="'mysql'">mysql</el-radio>
          <el-radio :value="'oracle'">oracle</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="表名">
        <el-input v-model="formData.tableName"></el-input>
      </el-form-item>
      <el-form-item label="表备注">
        <el-input v-model="formData.tableRemark" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="文件上传">
        <el-row :gutter="20">
          <el-col :span="18">
            <el-upload :action="uploadAction" :before-upload="beforeUpload" :on-success="handleUploadSuccess"
              :auto-upload="true" :limit="limit" :on-exceed="handleExceed" ref="uploadRef">
              <el-button type="primary">上传文件</el-button>
            </el-upload>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="downFileTemplate">模板下载</el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <!-- 如果需要显示文件ID，可以在这里添加输入框 -->
      <el-form-item label="文件ID">
        <el-input v-model="fileId" disabled placeholder="文件ID将在这里显示" readonly />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交表单</el-button>
      </el-form-item>
      <el-form-item label="SQL 语句">
        <el-input type="textarea" :rows="4" v-model="sqlStatement" readonly placeholder="这里将显示 SQL 语句"></el-input>
      </el-form-item>
    </el-form>

  </div>
</template>


<!-- 显式声明组件名称 -->
<!-- <script lang="ts">
export default {
    name: 'FileUpload',
};
</script> -->

<script setup>
import { ref } from 'vue';
import { ElForm, ElFormItem, ElInput, ElUpload, ElButton, ElMessage } from 'element-plus';
import axios from 'axios'; // 假设你使用axios作为HTTP库
const apiUrl = import.meta.env.VITE_APP_API_URL;
const uploadRef = ref(null);
const fileId = ref('');
const formRef = ref(null);
const sqlStatement = ref('');
const limit = 1;
const uploadAction = apiUrl + '/file/upload'; // 你的上传接口地址
const createSqlUrl = apiUrl + '/sql/generateOracleSql'; // 生成SQL语句的接口地址

const formData = ref({
  tableName: '',
  tableRemark: '',
  fileId: '', // 用于存储上传后的文件URL或标识符
  database: ''//数据库
});

// 上传文件之前的钩子，可以用来进行一些预处理或校验
const beforeUpload = (file) => {
  // 例如，限制文件大小为 10 MB
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    alert('文件大小不能超过 10 MB!');
    return false;
  }
  return true;
};

// 文件上传成功的钩子
const handleUploadSuccess = (response, file, fileList) => {
  // 假设后端返回的数据结构是 { fileId: 'xxx' }
  if (response) {
    //alert(`文件上传成功！文件ID: ${response}`);
    formData.value.fileId = response; // 将文件ID存储在表单数据中
    fileId.value = response; // 将文件ID显示在输入框中
  } else {
    alert('文件上传失败！');
  }
};

const handleExceed = (files, fileList) => {
  ElMessage.warning(`当前限制选择 ${limit} 个文件，本次选择了 ${files.length} 个文件，共选择了 ${fileList.length} 个文件`);
};
// 手动触发文件上传
const submitFile = () => {
  uploadRef.value.submit();
};
async function submitForm() {
  try {
    // 验证表单（如果有验证规则的话）
    // 发送POST请求提交表单数据（不包括文件，文件已经通过上传接口处理）
    const response = await axios.post(createSqlUrl, {
      tableName: formData.value.tableName,
      tableRemark: formData.value.tableRemark,
      fileId: formData.value.fileId, // 提交上传后文件的URL或标识符
      database: formData.value.database,
    });
    sqlStatement.value = response.data;
    ElMessage.success('SQL 语句生成成功！');
  } catch (error) {
    ElMessage.error('表单提交过程中发生错误', error);
  }
}
async function downFileTemplate() {
  try {
    // 发送GET请求到后端下载接口
    const response = await axios.get(apiUrl + `/file/download/670dee2c1fa4351beb7d6fe9`, {
      responseType: 'blob', // 告诉axios你期望的响应数据类型是blob
    });
    // 创建blob URL
    const url = window.URL.createObjectURL(new Blob([response.data]));
    // 创建a标签用于下载
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `生成sql模板.xlsx`); // 设置下载文件的名称
    document.body.appendChild(link);
    link.click(); // 模拟点击下载

    // 清理资源
    window.URL.revokeObjectURL(url);
    document.body.removeChild(link);
  } catch (error) {
    // 处理错误情况，例如显示错误消息给用户
    ElMessage.error('下载文件时发生错误:', error);
  }
}


// 清空表单的方法
const clearForm = () => {
  formData.database = '';
  formData.tableName = '';
  formData.tableRemark = '';
  fileId.value = '';
  sqlStatement.value = '';
  // 如果还有其他需要清空的数据，可以在这里添加
};

</script>

<style scoped></style>
