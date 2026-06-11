# 书库服务设计说明

> **结论先行**:书库服务(br-library-service)是书域 MVP-1，负责公版书的章节内容存取与搜索，对外提供 5 个 REST 接口。实体设计来源于旧 Android 项目 Room 实体简化——去掉网文专属字段，只保留公版书核心字段。

## 一、数据模型

**结论:四张表——books / chapters / paragraphs / tags，加上 book_tags 多对多关联——覆盖"一本书有多个章节，每个章节有多个段落，书可以打多个标签"的完整模型。**

**根据**:实体结构严格按 P5 BC-2 规格。Book 和 Tag 多对多（JPA `@ManyToMany`），Book 和 Chapter 一对多，Chapter 和 Paragraph 一对多——均通过外键关联而非 JPA 的 `@OneToMany` 映射。每张表都有自增主键、create_time、update_time、is_delete（逻辑删除，照抄 user-center 的三原则）。

**例子**:查询《西游记》详情时，BookService 走三步：`bookRepo.findById(1)` 查书籍 → `chapterRepo.findByBookId(1)` 查三章目录 → 返回 BookDetailResponse。不做 JPA 级联加载，每步查自己的 Repository，N+1 可控。

## 二、API 设计

**结论:五个接口覆盖"搜书 → 看书 → 读章节"的完整链路，统一返回 `{code, data, message}`。**

| 接口 | 用途 | 关键参数 |
|------|------|----------|
| `GET /api/books` | 分页列表 + 搜索 | `?q=` 模糊书名/作者，`?tag=` 标签过滤 |
| `GET /api/books/{id}` | 书籍详情 | 返回书名/作者/简介/标签/章节目录 |
| `GET /api/books/{id}/chapters` | 章节目录 | 返回该书的全部章节（按 seq 排序） |
| `GET /api/chapters/{id}` | 章节内容 | 返回该章的全部段落（按 seq 排序） |
| `GET /api/health` | 健康检查 | 返回 "br-library-service is up" |

**根据**:BaseResponse / ErrorCode / BusinessException / GlobalExceptionHandler 全部照抄 user-center 仓——格式校验过返回 40000，资源不存在返回 40400，未捕获异常统一 50000。Swagger 通过 `springdoc-openapi` 自动生成。

**例子**:`GET /api/books?q=西游&tag=名著&page=0&size=10` 实际 SQL 走 `BookRepository.searchByTag`，Hibernate 生成 `SELECT DISTINCT b FROM books b JOIN book_tags bt JOIN tags t WHERE ... t.name = '名著' ... LIMIT 10`。

## 三、种子数据

**结论:启动时若 books 表为空，从 `seed/books.json` 导入《西游记》前三回和《朝花夕拾》前两篇，共 61 段。**

**根据**:种子数据使用《西游记》（吴承恩，明代）和《朝花夕拾》（鲁迅，1926 年）——两本均为公版书，可用公开文本。JSON 结构为嵌套：书籍 → 标签列表 + 章节列表 → 段落列表。重启时 `bookRepo.count() > 0` 则跳过。

**例子**:首次启动日志：`Seeded 2 books, 61 paragraphs total.`。再次启动日志：`Seed data already present, skipping.`。`SELECT count(*) FROM paragraphs` = 61。

## 本章小结

- **结论**:书库服务 = 4 实体 + 5 接口 + 种子数据，照抄 user-center 的公共件骨架；
- **根据**:实体设计从旧 Android Room 实体简化，API 契约按 P5 BC-2 规格，返回格式照抄 user-center；
- **例子**:`?q=西游` 命中《西游记》，`?tag=散文` 命中《朝花夕拾》，`/books/999` 返回 40400。
