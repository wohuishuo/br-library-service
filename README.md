<div align="center">

# BookRealm Library Service

**公版书内容 API:把书拆成书、章、段、标签,给阅读 App 和 AI 服务复用**

这是一个可独立运行的 Spring Boot 服务。它专注一件事:把书的内容可靠地存好、查得出来、按稳定接口交给客户端或 RAG 服务。

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring_Data-JPA-59666C?style=flat-square&logo=hibernate&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
![Tests](https://img.shields.io/badge/tests-7_passing-48cfad?style=flat-square)

[BookRealm 平台书](https://wohuishuo.github.io/book-realm/) · [本服务实战章](https://wohuishuo.github.io/book-realm/project/library)

</div>

---

## 一分钟理解

**br-library-service 是阅读平台的内容底座。**

阅读 App 用它搜索书、打开目录、读取章节、保存划线和笔记;AI 服务用它拉取段落,再做摘要和原文问答。它不处理登录、不处理推荐、不处理 AI,边界越清楚,越容易独立复用。

```
Android App ── GET /api/books?q=西游 ──▶ br-library-service ──▶ MySQL
AI Service  ── GET /api/chapters/{id} ─▶       书 / 章 / 段 / 标签
```

## 数据模型

```
Book 1───* Chapter 1───* Paragraph
Book *───* Tag
```

段落单独建模,是为了让阅读器能按段展示,也让 RAG 服务能按段落引用原文。

## 快速开始

```powershell
# 1. 建库
mysql -u root -e "CREATE DATABASE IF NOT EXISTS book_realm_library DEFAULT CHARACTER SET utf8mb4;"

# 2. 启动
mvn spring-boot:run

# 3. 验证
curl http://localhost:8082/api/health
curl "http://localhost:8082/api/books?q=西游"
```

Swagger:<http://localhost:8082/api/swagger-ui.html>

内置种子数据:《西游记》前 3 回 + 《朝花夕拾》前 2 篇,共 61 段真实公版原文。

## API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 服务存活检查 |
| GET | `/api/books?q=&tag=&page=&size=` | 书籍列表,支持关键词、标签、分页;`page` 从 0 开始 |
| GET | `/api/books/{id}` | 书籍详情:简介、标签、章节目录 |
| GET | `/api/books/{id}/chapters` | 某书章节目录 |
| GET | `/api/chapters/{id}` | 章节内容,含全部段落 |
| POST | `/api/marks` | 保存段落级划线/笔记 |
| GET | `/api/chapters/{id}/marks?userId=` | 查询某章节的划线/笔记 |
| GET | `/api/books/{id}/marks?userId=` | 查询某书的划线/笔记 |
| DELETE | `/api/marks/{id}?userId=` | 删除划线/笔记 |

统一返回 `{ code, data, message }`,其中 `code=0` 表示成功。

## 在 BookRealm 中的位置

| 上游/下游 | 关系 |
| --- | --- |
| [br-reader-app](https://github.com/wohuishuo/br-reader-app) | 搜书、目录、阅读章节 |
| [br-ai-service](https://github.com/wohuishuo/br-ai-service) | 拉取段落,建立 RAG 索引 |
| [book-realm](https://github.com/wohuishuo/book-realm) | 平台总书和完整教学 |

## 文档

| 文档 | 内容 |
| --- | --- |
| [`docs/design.md`](docs/design.md) | 数据模型、API 设计、种子数据 |
| [`docs/notes.md`](docs/notes.md) | 真实踩坑和解决记录 |
| [平台书实战章](https://wohuishuo.github.io/book-realm/project/library) | 站在完整平台视角讲解本服务 |

## 测试

```powershell
mvn test
```
