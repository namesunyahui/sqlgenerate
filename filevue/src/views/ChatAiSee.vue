<template>
  <div class="container">
    <div class="input-container">
      <el-input v-model="input" type="textarea" placeholder="请输入内容" :rows="3" @keyup.enter="connectSSE"></el-input>
      <div class="button-group">
        <el-button type="primary" @click="connectSSE">请求</el-button>
        <el-button type="primary" @click="disconnectSSE">中止</el-button>

      </div>
    </div>
    <div v-html="parseMarkdown(output)" class="markdown-content"></div>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElInput, ElButton } from 'element-plus';
import MarkdownIt from 'markdown-it';
const input = ref('');
const output = ref('');
const apiUrl = import.meta.env.VITE_APP_API_URL;
const sseUrlBase = `${apiUrl}/ai/ai_generate_reply/sse?`;
let sseUrl; // 改为在connectSSE函数中动态生成

// 在connectSSE函数内部
sseUrl = sseUrlBase + 'requestMessage=' + input.value;


const md = new MarkdownIt();

function parseMarkdown(output) {
  return md.render(output);
}


let sse;

// 建立 SSE 连接的方法
const connectSSE = () => {


  sseUrl = sseUrlBase + 'requestMessage=' + input.value;
  output.value += '\n'+ `${input.value}` + '\n';
  input.value = '';
  if (!sse || sse.readyState === EventSource.CLOSED) {
    sse = new EventSource(sseUrl);
    sse.onmessage = (event) => {
      output.value += event.data;
    };

    sse.onerror = (error) => {
      console.error('SSE error:', error);
      sse.close();
    };
  } else {
    console.log('SSE 连接已建立');
  }
};

// 关闭 SSE 连接的方法
const disconnectSSE = () => {
  if (sse) {
    sse.close();
    sse = null; // 清除对 EventSource 实例的引用
    console.log('SSE 连接已关闭');
  } else {
    console.log('SSE 连接未建立');
  }
};


</script>


<style scoped>
/* 引入外部的 Markdown CSS 样式表，例如 GitHub 风格 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/4.0.0/github-markdown.min.css');


.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  /* Adjust based on your needs */
}

.input-container {
  display: flex;
  flex-direction: column;
  flex: 0 0 15%;
  /* 输入框占据15%的高度 */
}

.button-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
  /* 根据需要调整按钮与输入框的间距 */
}



/* 你可以在这里添加额外的自定义样式 */
.markdown-content {
  /* 例如，设置最大宽度、边距等 */
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}
</style>
