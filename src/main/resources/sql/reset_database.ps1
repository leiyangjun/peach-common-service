# PostgreSQL：全量删表重建 + 种子（顺序执行 init_table.sql → init_data.sql）
# 与 peach-common-service 的 application.yml 一致：默认库 peach_common、schema public（脚本内 SQL 已写 public）。
#
# 推荐：在 PowerShell 中设置与 Spring 相同的环境变量后执行（可与 CI 对齐）：
#   $env:DB_URL = "jdbc:postgresql://192.168.99.100:5432/peach_common?currentSchema=public"
#   $env:DB_USERNAME = "postgres"
#   $env:DB_PASSWORD = "postgres"
#   .\reset_database.ps1
#
# 亦可直接使用 libpq 变量（优先级低于 DB_* 解析结果中的主机库名）：
#   $env:PGHOST="127.0.0.1"; $env:PGPORT="5432"; $env:PGDATABASE="peach_common"
#   $env:PGUSER="postgres"; $env:PGPASSWORD="secret"
#   .\reset_database.ps1
#
# 依赖：PATH 上可执行 psql（PostgreSQL 客户端）。

$ErrorActionPreference = "Stop"
$sqlDir = $PSScriptRoot

if ($env:DB_URL) {
    $m = [regex]::Match($env:DB_URL, 'jdbc:postgresql://([^:/]+)(?::(\d+))?/([^?&#]+)')
    if ($m.Success) {
        $env:PGHOST = $m.Groups[1].Value
        if ($m.Groups[2].Success -and $m.Groups[2].Value) {
            $env:PGPORT = $m.Groups[2].Value
        }
        elseif (-not $env:PGPORT) {
            $env:PGPORT = "5432"
        }
        $env:PGDATABASE = $m.Groups[3].Value
    }
    else {
        Write-Warning "DB_URL 存在但非 jdbc:postgresql:// 格式，已忽略 URL 解析，改用 PG* / 默认值。"
    }
}
if ($env:DB_USERNAME) {
    $env:PGUSER = $env:DB_USERNAME
}
if ($null -ne $env:DB_PASSWORD) {
    $env:PGPASSWORD = $env:DB_PASSWORD
}

if (-not $env:PGHOST) {
    $env:PGHOST = "192.168.99.100"
}
if (-not $env:PGPORT) {
    $env:PGPORT = "5432"
}
if (-not $env:PGDATABASE) {
    $env:PGDATABASE = "peach_common"
}
if (-not $env:PGUSER) {
    $env:PGUSER = "postgres"
}

$psql = Get-Command psql -ErrorAction SilentlyContinue
if (-not $psql) {
    Write-Error "未在 PATH 中找到 psql。请安装 PostgreSQL 客户端或将 psql 加入 PATH 后再执行。"
    exit 1
}

$initTable = Join-Path $sqlDir "init_table.sql"
$initData = Join-Path $sqlDir "init_data.sql"
if (-not (Test-Path $initTable) -or -not (Test-Path $initData)) {
    Write-Error "缺少 SQL 文件：$initTable 或 $initData"
    exit 1
}

Write-Host "连接: host=$($env:PGHOST) port=$($env:PGPORT) db=$($env:PGDATABASE) user=$($env:PGUSER)"
Write-Host "执行: $initTable 然后 $initData"

& psql -h $env:PGHOST -p $env:PGPORT -U $env:PGUSER -d $env:PGDATABASE -v ON_ERROR_STOP=1 -f $initTable -f $initData
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
Write-Host "完成：表结构已按 init_table.sql 重建，种子已按 init_data.sql 写入。"
