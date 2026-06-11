# br-library-service

**书域 MVP-1 · 书库服务**

## 快速开始

```powershell
# 1. 先建库
mysql -u root -e "CREATE DATABASE IF NOT EXISTS book_realm_library DEFAULT CHARACTER SET utf8mb4;"

# 2. 启动
cd br-library-service
mvn spring-boot:run

# 3. 验证
curl http://localhost:8082/api/health
# → {"code":0,"data":"br-library-service is up","message":"ok"}
```

## API 一览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 健康检查 | GET | `/api/health` | 服务存活 |
| 书籍列表 | GET | `/api/books?q=&tag=&page=&size=` | 分页搜索 |
| 书籍详情 | GET | `/api/books/{id}` | 单本书 |
| 章节目录 | GET | `/api/books/{id}/chapters` | 某书的全部章节 |
| 章节内容 | GET | `/api/chapters/{id}` | 含段落列表 |

Swagger: `http://localhost:8082/api/swagger-ui.html`
