<template>
    <div class="ai-chat">
        <el-input v-model="message" placeholder="请输入你的问题" @keyup.enter="sendMessage"></el-input>
        <el-button type="primary" @click="sendMessage">发送</el-button>

        <div class="chat-history">
            <div v-for="(item, index) in chatHistory" :key="index" class="message-item">
                <!-- <span class="message-text">{{ item.text }}</span>
                <span class="message-type">{{ item.type }}</span> -->
                <span class="message-text" v-html="parseMarkdown(item.text)"></span>
                <span class="message-type">{{ item.type }}</span>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElInput, ElButton } from 'element-plus';
import MarkdownIt from 'markdown-it';
import axios from 'axios'; // 假设你使用axios作为HTTP库
const apiUrl = import.meta.env.VITE_APP_API_URL;
const aiMessageUrl = apiUrl + '/ai/chat';
const md = new MarkdownIt();

const message = ref('');
const chatHistory = ref([
    { text: '欢迎使用AI应答系统！', type: 'system' },
]);

const sendMessage = async () => {
    if (message.value.trim() !== '') {
        // 将用户消息添加到聊天历史记录中
        chatHistory.value.push({ text: message.value, type: 'user' });

        try {
            // 使用axios发起GET请求，携带用户消息作为参数
            // 假设你的后端接口接受一个名为'message'的查询参数
            const response = await axios.get(aiMessageUrl, {
                params: { message: message.value } // 将用户消息作为查询参数发送
            });

            // 处理响应数据，假设后端返回的数据结构为{ text: 'AI的回答' }
            const aiResponse = response.data; // 或者根据你的后端实际返回结构调整

            // 将AI的回应添加到聊天历史记录中
            chatHistory.value.push({ text: aiResponse, type: '通义千问' });

            // 清空输入框
            message.value = '';
        } catch (error) {
            // 处理请求错误，例如网络问题或后端服务不可用等
            console.error('Error fetching AI response:', error);
            // 你可以在这里添加错误处理逻辑，比如向用户显示一个错误消息
        }
    }
};

function parseMarkdown(text) {
    return md.render(text);
}

</script>

<style scoped>
.ai-chat {
    display: flex;
    flex-direction: column;
    height: 100vh;
    padding: 20px;
}

.chat-history {
    flex: 1;
    margin-top: 20px;
    padding: 10px;
    background-color: #f5f5f5;
    border-radius: 10px;
    overflow-y: auto;
}

.message-item {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin-bottom: 10px;
    background-color: #7bf11b;
}

.message-text {
    margin-right: 10px;
    padding: 5px;
}

.message-type {
    font-size: 12px;
    color: #214aef;
}
</style>