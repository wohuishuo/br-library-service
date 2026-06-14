<div align="center">

# 📚 书库服务 · br-library-service

**[书域 BookRealm](https://github.com/wohuishuo/book-realm) 电子书平台的内容服务**

书域是一个拆成 5 个独立模块的电子书平台;本仓是其中的"内容底座":
把公版书按「书 → 章 → 段」结构化存好,对外提供干净的 REST 接口——
阅读 App 从这里拿书,AI 问答服务从这里取段落做向量化。

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![JPA](https://img.shields.io/badge/Spring_Data-JPA-59666C?style=flat-square&logo=hibernate&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
![Tests](https://img.shields.io/badge/tests-6_passing-48cfad?style=flat-square)

[平台总书](https://github.com/wohuishuo/book-realm) · [本服务实战章讲解](https://github.com/wohuishuo/book-realm/blob/main/docs/project/library.md)

</div>

---

## 这是什么

**一句话:一个只管"书的内容"的 Spring Boot 微服务。** 它不碰登录、不碰前端,职责单一——把书可靠地存好、查得出来。这是有意为之的边界(领域驱动设计里的 Bounded Context),好处是能独立开发、独立部署、被多个服务复用。

```
                 GET /api/books?q=西游
   阅读 App  ───────────────────────────▶  ┌─────────────────┐
   AI 服务   ───────────────────────────▶  │  书库服务 :8082  │ ──▶ MySQL
                 GET /api/chapters/{id}      └─────────────────┘   (书/章/段/标签)
```

## 数据模型

```
Book 1───* Chapter 1───* Paragraph         Book *───* Tag
（书）        （章）        （段落）                  （标签）
```

一本书有多个章,一章有多个段落,书可打多个标签。段落单独成表,是为了让 AI 服务能按段落做精准检索。

## 🚀 快速开始

```powershell
# 1. 建库(本机 MySQL,scoop 安装,root 无密码)
mysql -u root -e "CREATE DATABASE IF NOT EXISTS book_realm_library DEFAULT CHARACTER SET utf8mb4;"

# 2. 启动(首次启动自动建表 + 导入种子书)
mvn spring-boot:run

# 3. 验证
curl http://localhost:8082/api/health
# → {"code":0,"data":"br-library-service is up","message":"ok"}

curl "http://localhost:8082/api/books?q=西游"
# → 返回《西游记》,带标签 ["神魔","古典","名著"]
```

启动后 **Swagger 在线调试**:<http://localhost:8082/api/swagger-ui.html>

> 开箱即用:内置《西游记》前 3 回 + 《朝花夕拾》前 2 篇,共 61 段真实公版原文,克隆即可跑,无需手动造数据。

## API 一览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | 服务存活检查 |
| GET | `/api/books?q=&tag=&page=&size=` | 书籍列表(书名模糊 + 标签过滤 + 分页,`page` 从 0 开始) |
| GET | `/api/books/{id}` | 书籍详情(简介 + 标签 + 章节目录) |
| GET | `/api/books/{id}/chapters` | 某书的全部章节目录 |
| GET | `/api/chapters/{id}` | 章节内容(含全部段落) |

统一返回 `{ code, data, message }`(0 = 成功)。

## 📖 项目文档

| 文档 | 内容 |
|------|------|
| [`docs/design.md`](docs/design.md) | **设计说明** —— 数据模型、API 设计、种子数据,讲清"为什么这么做" |
| [`docs/notes.md`](docs/notes.md) | **实现笔记** —— 开发中真实踩的坑(MockMvc 路径、JPA 懒加载等)及解决 |
| [平台书 · MVP-1 实战章](https://github.com/wohuishuo/book-realm/blob/main/docs/project/library.md) | 站在整个平台视角讲这个服务怎么做出来的 |

## 技术栈

Java 21 · Spring Boot 3.3 · Spring Data JPA · MySQL 8 · springdoc(Swagger)· JUnit 5(6 个测试:Repository 2 + MockMvc 4)

## 运行测试

```powershell
mvn test     # 需本机 MySQL 在运行
```

---

<div align="center">

「书域」超级项目 · 5 个互相咬合的 MVP 之一 · [回到平台总书](https://github.com/wohuishuo/book-realm)

</div>
