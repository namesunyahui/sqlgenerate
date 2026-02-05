# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Language Requirement

**IMPORTANT**: All responses to the user must be in **Chinese (中文)**. This is a strict requirement for all communication.

## Project Overview

**sqlgenerate** is a full-stack AI-powered SQL generation application with a Spring Boot backend and Vue.js 3 frontend. Users upload Excel files containing table definitions, and the app generates CREATE TABLE statements for MySQL and Oracle databases. It also integrates with multiple AI providers (Zhipu AI and Alibaba Tongyi) for AI-powered question generation and chat.

## Development Commands

### Backend (Spring Boot + Maven)
```bash
# Build the project
mvn clean package

# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=TestZhipu

# Run the application (default port: 8081)
mvn spring-boot:run

# Or run the JAR directly
java -jar target/sqlgenerate-0.0.1-SNAPSHOT.jar
```

### Frontend (Vue 3 + Vite)
```bash
cd filevue

# Install dependencies
npm install

# Development server (runs on port 5273)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# TypeScript type checking
npm run type-check
```

### API Documentation
Once the backend is running, access Swagger UI at:
```
http://localhost:8081/swagger-ui/index.html
```

## Architecture

### Backend Structure
The backend follows a **layered architecture** with reactive programming support:

- **Controller Layer** (`src/main/java/org/example/sqlgenerate/*/controller/`): REST API endpoints
  - `SqlGenerateController` - SQL generation endpoints
  - `FileManageController` - File upload/download (MongoDB GridFS)
  - `AliChatController` - Ali Tongyi AI chat endpoints
  - `FluxTestController` - Reactive streaming tests

- **Service Layer** (`src/main/java/org/example/sqlgenerate/*/service/`): Business logic
  - Interface-based design with `Impl/` subpackages for implementations
  - `GenerateSqlService` - Core SQL generation logic
  - `AiZhipuService` / `AliTongYiService` - AI provider integrations
  - `FileStorageService` - GridFS file storage operations

- **Repository Layer** (`src/main/java/org/example/sqlgenerate/mongodb/service/`): Data access
  - `FileInfoRepository` - MongoDB repository for file metadata

### Key Modules

1. **SQL Generation** (`createSql/` package):
   - Parses Excel files using EasyExcel
   - Generates CREATE TABLE statements for MySQL and Oracle
   - Model: `ImportExcel` represents Excel column definitions (fieldName, fieldType, length, isNull, key, default, remarks)

2. **AI Integration** (`ai/` package):
   - Supports multiple AI providers: Zhipu AI and Alibaba Tongyi
   - Synchronous and streaming (SSE) chat responses
   - `AiManager` - Unified interface for Zhipu API calls
   - Reactive streaming with Project Reactor (Flowable)

3. **File Storage** (`mongodb/` package):
   - MongoDB GridFS for large file storage
   - `FileInfo` entity for file metadata
   - Two upload methods: simple storage and GridFS

### Frontend Structure
Vue 3 SPA with Composition API, located in `filevue/`:
- `src/views/FileUpload.vue` - Excel upload interface
- `src/views/ChatAI.vue` - AI chat interface
- `src/views/ChatAiSee.vue` - Streaming AI chat interface
- Uses Element Plus for UI components
- Axios for HTTP communication with backend

## Configuration

Configuration is in `src/main/resources/application.yml`:

- Backend runs on port **8081**
- MongoDB connection (GridFS for file storage)
- Redis (for vector storage/caching)
- AI API keys (Zhipu AI, Alibaba Tongyi)
- Swagger UI enabled at `/swagger-ui/index.html`

## Key Dependencies

**Backend:**
- Spring Boot 3.3.3 (Java 17)
- Spring WebFlux (reactive support)
- Spring Data MongoDB
- EasyExcel 3.2.1 (Excel parsing)
- Zhipu AI SDK (V4-2.3.0)
- Alibaba Dashscope SDK 2.16.8
- Knife4j 4.4.0 (Swagger UI)

**Frontend:**
- Vue 3.5.11
- Vite 5.4.8
- TypeScript 5.5.4
- Element Plus 2.8.4
- Axios 1.7.7

## API Endpoints

- `POST /sql/generateOracleSql` - Generate SQL from Excel file
- `POST /file/upload` - Upload file using GridFS
- `GET /file/download/{fileId}` - Download file by ID
- `GET /ai/chat?message={message}` - Chat with Ali Tongyi
- `POST /ai/ai_generate` - Generate AI questions (non-streaming)
- `GET /ai/ai_generate/sse` - Generate questions (SSE streaming)
- `GET /ai/ai_generate_reply/sse?requestMessage={message}` - Stream AI reply

## Testing

Test class `TestZhipu.java` includes:
- `testChatMessage()` - Test Zhipu AI chat
- `testZhipuSeeChat()` - Test streaming responses
- `callDoStreamRequest()` - Test reactive streaming
- `testEmbedding()` - Test vector embeddings (512 dimensions)

## Important Notes

- The Excel format for SQL generation expects specific columns: field name, type, length, nullability, primary key, default value, remarks
- Streaming AI responses use Server-Sent Events (SSE) with Flowable
- MongoDB GridFS is used for large file storage; simple MongoDB storage is also available
- Vue frontend dev server runs on port 5273; backend on 8081
