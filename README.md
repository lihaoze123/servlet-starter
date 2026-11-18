# Servlet Starter

[![CI](https://github.com/lihaoze123/servlet-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/lihaoze123/servlet-starter/actions/workflows/ci.yml)

基于 Jakarta Servlet 的 Java Web 起步项目，包含 JSP 首页、JSON 用户 API、MySQL 数据访问、HikariCP 连接池，以及 Filter 和 Listener 示例。

## 技术栈

- Java 25、Maven Wrapper
- Jakarta Servlet 6.1、JSP、Jetty 12.1
- MySQL、JDBC、HikariCP
- Jackson、Lombok、SLF4J / Logback

## 快速启动

准备 JDK 25，并确保 `JAVA_HOME` 和 `PATH` 指向该 JDK。使用 Docker Compose 启动本地 MySQL：

```bash
gh repo clone lihaoze123/servlet-starter
cd servlet-starter

# 替换为你自己的本地开发口令；应用和 MySQL 使用同一个值。
export DB_PASSWORD='replace-with-your-local-password'
docker compose up -d --wait
./mvnw jetty:run
```

访问 <http://localhost:8080/> 查看 JSP 首页，访问 <http://localhost:8080/api/users> 查看用户列表。

Compose 首次启动时自动执行 `src/main/resources/schema.sql`，创建 `demo` 数据库、`users` 表和两条示例数据。已有数据卷不会重复初始化。停止数据库可执行 `docker compose down`；数据保存在命名卷中。上述数据库 root 账号仅供本地开发，部署时请创建权限受限的应用账号。

如果已有 MySQL，可手动执行 `schema.sql`，然后设置数据库环境变量再启动应用。Windows 使用 `mvnw.cmd`，并通过 PowerShell 的 `$env:DB_PASSWORD` 等设置环境变量。

## 配置

默认配置位于 `src/main/resources/db.properties`，以下环境变量可覆盖配置：

| 变量 | 用途 |
| --- | --- |
| `DB_URL` | JDBC URL，默认连接本机 3306 端口的 `demo` 数据库 |
| `DB_USER` | 数据库用户名，默认 `root` |
| `DB_PASSWORD` | 数据库密码，默认空值；请通过环境变量提供 |

例如：

```bash
export DB_URL='jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true'
export DB_USER='root'
export DB_PASSWORD='replace-with-your-local-password'
./mvnw jetty:run
```

不要将实际数据库口令写入仓库。Jetty 开发启动方式参见 [官方 Maven Plugin 文档](https://jetty.org/docs/jetty/12.1/programming-guide/maven-jetty/jetty-maven-plugin.html)。

## API 示例

```bash
# 列出用户
curl http://localhost:8080/api/users

# 按 ID 查询；不存在时返回 404
curl 'http://localhost:8080/api/users?id=1'

# 创建用户；成功时返回 201 和创建后的用户
curl -i http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"username":"charlie","email":"charlie@example.com"}'
```

非法 ID、无效 JSON 或缺少用户名 / 邮箱时返回 400；未处理异常由 Filter 记录并返回通用 500 JSON 响应。

## 项目结构

```text
src/main/java/org/example/demo/
├── dao/UserDAO.java                 # JDBC 查询与插入
├── filter/ApiFilter.java            # JSON、UTF-8 与异常处理
├── listener/AppContextListener.java # 连接池初始化与关闭
├── model/User.java                  # 用户模型
└── servlet/UserServlet.java         # /api/users
src/main/resources/
├── db.properties
├── logback.xml
└── schema.sql
src/main/webapp/
├── WEB-INF/web.xml
└── index.jsp
```

## 构建

```bash
./mvnw -B -ntp clean verify
```

产物为 `target/servlet-starter-1.0-SNAPSHOT.war`，可部署到支持 Jakarta Servlet 6.1 的容器。GitHub Actions 对 `main` 分支推送和 Pull Request 执行构建。当前尚无自动化测试。

提交信息采用 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/v1.0.0/)，例如 `feat: 添加健康检查接口`。
