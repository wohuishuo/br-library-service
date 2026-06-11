# 实现笔记

## MockMvc 路径问题

MockMvc 测试时不走 `server.servlet.context-path=/api`（只在真实 Tomcat 中生效），所以测试中路径写 `/books` 而不是 `/api/books`。生产环境 `curl http://localhost:8082/api/books` 正常，Postman/Swagger 也走 `/api/books`。

## LazyInitializationException

JPA `@ManyToMany` 默认懒加载。在 `@Transactional(readOnly = true)` 方法内调用 `b.getTags().size()` 触发初始化，否则序列化 JSON 时报错。Repository 查询加 `LEFT JOIN FETCH b.tags` 也可以解决，但分页 + FETCH JOIN 有 Hibernate 警告，改为事务内主动触发。

## MySQL 端口

本机 MySQL 是 scoop 安装，通过 `mysqld --standalone` 手动启动，监听 `localhost:3306`（非 3307）。root 用户无密码。

## JPA ddl-auto: update

开发期用 `update` 自动建表/加列，后续上线切 `validate`。`@Column(columnDefinition = "TEXT")` 用于 intro 和 content 字段避免 varchar(255) 默认长度不够。
