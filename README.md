# Keycloak LDAP SSHA 迁移插件

用于将 OpenLDAP 的 SSHA 密码迁入 Keycloak，保留用户原密码。首次成功登录后，密码自动升级为 Realm 当前策略指定的算法（默认 Argon2）

插件负责校验已导入的本地密码，账号迁移和 LDAP 关联处理需另行完成。

使用 JDK 21 和 Maven 构建：

```sh
mvn package
```

产物：`target/ldap-ssha-migration-provider-0.1.0-SNAPSHOT.jar`。

导入凭据格式：

```json
{
  "type": "password",
  "temporary": false,
  "credentialData": "{\"algorithm\":\"ldap-ssha\",\"hashIterations\":1}",
  "secretData": "{\"value\":\"摘要的 Base64\",\"salt\":\"原盐的 Base64\"}"
}
```

将原 `{SSHA}` 后的 Base64 解码，前 20 字节为摘要，其余为盐；分别重新编码为标准 Base64。

- `credentialData` 和 `secretData` 是 JSON 字符串，不要设置外层 `value`。
- Realm 保持使用 Argon2 等内置密码策略，不要选择 `ldap-ssha`。
- 所有 SSHA 凭据升级或重置后，才能移除插件，因为未登录用户不会自动升级。
