# peach-common-service

Peach 体系中的**基础业务服务**工程，与 `peach-gateway`、`peach-auth-service`、`peach-dependencies` 位于同级目录。本模块使用 **`peach-dependencies` BOM** 统一第三方与 Spring 生态版本，自身 **不向 Maven Central 发布**（内部/私有构建即可）。

> **认证**：用户登录与 JWT 签发已独立为 **`peach-auth-service`**（`spring.application.name=peach-auth-service`），本服务不再包含认证相关代码与依赖。

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
- 访问令牌由网关校验；业务接口依赖网关注入的查询参数（与 `CurrentLoginUserUtil` 约定一致）。

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
