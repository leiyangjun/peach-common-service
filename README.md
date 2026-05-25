# peach-common-service

Peach 体系中的**基础业务服务**工程，与 `peach-gateway`、`peach-auth-service`、`peach-dependencies` 位于同级目录。本模块使用 **`peach-dependencies` BOM** 统一第三方与 Spring 生态版本，自身 **不向 Maven Central 发布**（内部/私有构建即可）。

> **认证**：用户登录与 JWT 签发已独立为 **`peach-auth-service`**（`spring.application.name=peach-auth-service`），本服务不再包含认证相关代码与依赖。

## 工程说明

承载菜单、用户、角色、权限等**基础域 API** 的骨架与实现入口；通过 **`peach-common-start`** 统一响应模型与 MyBatis 能力，经 **Nacos** 注册为 **`peach-common-service`**，由网关以 **`lb://peach-common-service`** 或 **`/peach-common-service/**`** 形式对外。

## 功能说明

- 用户等 REST API（详见 `org.peach.common.web` 等包）。
- 与网关协作：网关校验 JWT 后将身份信息以 **`peach_*` 查询参数**转发至本服务，业务代码通过 **`UserContext`**（来自 `peach-common-start`）读取。
- **读写分离**：`application.yml` 内含 **`spring.datasource.rw.*`** 方案说明，由 `peach-common-start` 的 `ReadWriteDataSourceAutoConfiguration` 接管。

## 技术栈

- JDK 21
- Spring Boot 4.0.x（与 BOM 对齐）
- `peach-common-start`：Web / MyBatis / 统一 `ApiResult` 等
- PostgreSQL（默认，可通过配置改为 H2 等）

## 工程坐标

| 项 | 值 |
| --- | --- |
| `groupId` | `org.peach.common` |
| `artifactId` | `peach-common-service` |
| 启动类 | `org.peach.common.CommonApp` |

## 当前范围

- 提供用户等业务 API 骨架；具体功能以代码为准。
- 访问令牌由**网关**校验；业务接口依赖网关注入的查询参数（与 **`org.peach.common.utils.UserContext`** 约定一致）。

## 环境变量（摘要）

| 变量 | 含义 |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | 激活配置，默认 `dev` |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | 主数据源（PostgreSQL） |
| `DB_WRITE_*` / `DB_READ_*` | 读写分离开启时的写/从库连接（见 `application.yml`） |
| `NACOS_*` | 与 `peach-auth-service` 相同的一组 Nacos 变量 |

## 开发约定

- 启动类使用 **`@PeachCloud`**（来自 `peach-common-start`），并**显式**依赖 `spring-cloud-starter-alibaba-nacos-discovery`（已在 `pom.xml`）。
- 保持 **`spring.application.module-code: COMM`**（四位）与错误码体系一致。

## 本地运行

前置：本机已安装 JDK 21，且本地 Maven 能解析 `org.peach.pom:peach-dependencies:0.0.1-SNAPSHOT` 与 `org.peach.common:peach-common-start:0.0.1-SNAPSHOT`（需先对 sibling 工程执行 `mvn install`，或配置私服）。

```bash
mvn -f peach-common-service/pom.xml spring-boot:run
```

默认端口：`8083`（见 `src/main/resources/application.yml`）。

## 构建与测试

```bash
mvn -f peach-common-service/pom.xml clean verify
```

## 与网关的关系

上线后可通过 `peach-gateway` 将对外路径转发至本服务（例如 `lb://peach-common-service`），具体路由以网关配置为准。

## 远程仓库与双推送

已配置 `origin` 同时推送 **GitHub** 与 **Gitee**，一次推送即可同步：

```bash
git push -u origin main
```

（若远程已有初始提交，请先按平台提示完成 `pull`/`merge` 或使用空仓库首次推送。）

## 作者

leiyangjun
