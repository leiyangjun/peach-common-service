#!/usr/bin/env bash
# PostgreSQL：全量删表重建 + 种子（init_table.sql → init_data.sql）
# 与 application.yml 对齐时可使用与 Spring 相同的环境变量：
#   export DB_URL="jdbc:postgresql://192.168.99.100:5432/peach_common?currentSchema=public"
#   export DB_USERNAME="postgres"
#   export DB_PASSWORD="postgres"
#   bash reset_database.sh
#
# 亦支持标准 libpq 变量：PGHOST、PGPORT、PGDATABASE、PGUSER、PGPASSWORD

set -euo pipefail
SQL_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ -n "${DB_URL:-}" ]]; then
  if [[ "$DB_URL" =~ jdbc:postgresql://([^:/]+)(:([0-9]+))?/([^?&#]+) ]]; then
    export PGHOST="${BASH_REMATCH[1]}"
    if [[ -n "${BASH_REMATCH[3]:-}" ]]; then
      export PGPORT="${BASH_REMATCH[3]}"
    elif [[ -z "${PGPORT:-}" ]]; then
      export PGPORT="5432"
    fi
    export PGDATABASE="${BASH_REMATCH[4]}"
  else
    echo "警告: DB_URL 非 jdbc:postgresql:// 格式，跳过 URL 解析。" >&2
  fi
fi
[[ -n "${DB_USERNAME:-}" ]] && export PGUSER="$DB_USERNAME"
[[ -n "${DB_PASSWORD:-}" ]] && export PGPASSWORD="$DB_PASSWORD"

export PGHOST="${PGHOST:-192.168.99.100}"
export PGPORT="${PGPORT:-5432}"
export PGDATABASE="${PGDATABASE:-peach_common}"
export PGUSER="${PGUSER:-postgres}"

command -v psql >/dev/null 2>&1 || { echo "错误: 未找到 psql" >&2; exit 1; }

echo "连接: host=$PGHOST port=$PGPORT db=$PGDATABASE user=$PGUSER"
echo "执行: $SQL_DIR/init_table.sql 然后 init_data.sql"

psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 \
  -f "$SQL_DIR/init_table.sql" \
  -f "$SQL_DIR/init_data.sql"

echo "完成：表结构已重建，种子已写入。"
