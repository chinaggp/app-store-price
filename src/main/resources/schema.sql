-- 1. 应用基础信息表
CREATE TABLE IF NOT EXISTS app_info (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    app_id      TEXT    NOT NULL,
    name        TEXT    NOT NULL,
    subtitle    TEXT    DEFAULT '',
    developer   TEXT    DEFAULT '',
    icon_url    TEXT    DEFAULT '',
    category_id TEXT    DEFAULT '',
    category_name TEXT  DEFAULT '',
    rating      REAL    DEFAULT 0,
    review_count TEXT   DEFAULT '',
    created_at  TEXT    NOT NULL,
    updated_at  TEXT    NOT NULL,
    UNIQUE(app_id)
);

-- 2. 价格快照表
CREATE TABLE IF NOT EXISTS price_snapshot (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    app_id      TEXT    NOT NULL,
    area_code   TEXT    NOT NULL,
    currency_code TEXT  NOT NULL,
    price       REAL    NOT NULL,
    cny_price   REAL    NOT NULL,
    snapshot_date TEXT  NOT NULL,
    created_at  TEXT    NOT NULL,
    UNIQUE(app_id, area_code, snapshot_date)
);

-- 3. 分类表
CREATE TABLE IF NOT EXISTS category (
    id          TEXT    PRIMARY KEY,
    name        TEXT    NOT NULL,
    description TEXT    DEFAULT '',
    icon        TEXT    DEFAULT '',
    sort_order  INTEGER DEFAULT 0,
    created_at  TEXT    NOT NULL
);

-- 4. 关注列表
CREATE TABLE IF NOT EXISTS watched_app (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    app_id      TEXT    NOT NULL UNIQUE,
    name        TEXT    NOT NULL,
    category_id TEXT    DEFAULT '',
    enabled     INTEGER DEFAULT 1,
    created_at  TEXT    NOT NULL
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_price_snapshot_app_date ON price_snapshot(app_id, snapshot_date);
CREATE INDEX IF NOT EXISTS idx_price_snapshot_date ON price_snapshot(snapshot_date);
CREATE INDEX IF NOT EXISTS idx_app_info_category ON app_info(category_id);
