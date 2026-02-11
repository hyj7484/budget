-- ============================================================
-- BudgetBook Schema (PostgreSQL)
-- - Designed for: Raspberry Pi Docker Postgres
-- - Idempotent-ish: enum은 DO $$, table은 IF NOT EXISTS 사용
-- ============================================================

BEGIN;

-- 1) Extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto; -- gen_random_uuid()

-- 2) Common trigger to auto-update updated_at
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;

-- 3) Enums
DO $$ BEGIN
  CREATE TYPE user_role AS ENUM ('OWNER','EDITOR','VIEWER');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE payment_method_type AS ENUM ('CARD','CASH','BANK','TRANSIT');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE category_kind AS ENUM ('EXPENSE','INCOME');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE category_status AS ENUM ('ACTIVE','HIDDEN','ARCHIVED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE tx_type AS ENUM ('EXPENSE','INCOME','TRANSFER');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE tx_status AS ENUM ('UNCATEGORIZED','CATEGORIZED','RECURRING','CONFIRMED','VOID');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE rule_field AS ENUM ('MERCHANT','MEMO');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE rule_operator AS ENUM ('CONTAINS','EQUALS','STARTS_WITH','ENDS_WITH','REGEX');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE budget_period_type AS ENUM ('MONTH','WEEK','YEAR','CUSTOM');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE notify_freq AS ENUM ('OFF','DAILY','WEEKLY','MONTHLY');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- ============================================================
-- 4) Tables
-- ============================================================

-- Users
CREATE TABLE IF NOT EXISTS app_user (
  id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email            text NOT NULL UNIQUE,
  password_hash    text, -- 소셜로그인만 쓰면 NULL 가능
  display_name     text NOT NULL,
  locale           text NOT NULL DEFAULT 'ko-KR',
  timezone         text NOT NULL DEFAULT 'Asia/Tokyo',
  default_currency char(3) NOT NULL DEFAULT 'JPY',
  created_at       timestamptz NOT NULL DEFAULT now(),
  updated_at       timestamptz NOT NULL DEFAULT now()
);

-- Ledgers (가계부/원장)
CREATE TABLE IF NOT EXISTS ledger (
  id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_user_id    uuid NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
  name             text NOT NULL,
  base_currency    char(3) NOT NULL DEFAULT 'JPY',
  timezone         text NOT NULL DEFAULT 'Asia/Tokyo',
  created_at       timestamptz NOT NULL DEFAULT now(),
  updated_at       timestamptz NOT NULL DEFAULT now()
);

-- Ledger members (공유/권한)
CREATE TABLE IF NOT EXISTS ledger_member (
  ledger_id   uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,
  user_id     uuid NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  role        user_role NOT NULL DEFAULT 'EDITOR',
  created_at  timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (ledger_id, user_id)
);

-- Payment methods (결제수단/계좌)
CREATE TABLE IF NOT EXISTS payment_method (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id     uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,
  type          payment_method_type NOT NULL,
  name          text NOT NULL,
  institution   text,
  last4         text,
  is_active     boolean NOT NULL DEFAULT true,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now()
);

-- Categories (트리 구조)
CREATE TABLE IF NOT EXISTS category (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id     uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,
  kind          category_kind NOT NULL,
  parent_id     uuid REFERENCES category(id) ON DELETE SET NULL,
  name          text NOT NULL,
  icon          text,
  keywords      text[] NOT NULL DEFAULT '{}',
  status        category_status NOT NULL DEFAULT 'ACTIVE',
  sort_order    int NOT NULL DEFAULT 0,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  UNIQUE (ledger_id, kind, parent_id, name)
);

-- Tags
CREATE TABLE IF NOT EXISTS tag (
  id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id  uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,
  name       text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (ledger_id, name)
);

-- Transactions (거래)
CREATE TABLE IF NOT EXISTS tx (
  id                     uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id               uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,

  occurred_at             timestamptz NOT NULL,
  type                   tx_type NOT NULL,
  status                 tx_status NOT NULL DEFAULT 'UNCATEGORIZED',

  currency_code           char(3) NOT NULL DEFAULT 'JPY',
  amount                  numeric(14,2) NOT NULL CHECK (amount >= 0),

  merchant                text,
  memo                    text,
  is_favorite             boolean NOT NULL DEFAULT false,
  confidence              smallint NOT NULL DEFAULT 100 CHECK (confidence BETWEEN 0 AND 100),

  payment_method_id       uuid REFERENCES payment_method(id) ON DELETE SET NULL,
  category_id             uuid REFERENCES category(id) ON DELETE SET NULL,

  from_payment_method_id  uuid REFERENCES payment_method(id) ON DELETE SET NULL,
  to_payment_method_id    uuid REFERENCES payment_method(id) ON DELETE SET NULL,

  created_at              timestamptz NOT NULL DEFAULT now(),
  updated_at              timestamptz NOT NULL DEFAULT now(),
  deleted_at              timestamptz,

  -- 타입별 필드 규칙
  CHECK (
    (type IN ('EXPENSE','INCOME') AND from_payment_method_id IS NULL AND to_payment_method_id IS NULL)
    OR
    (type = 'TRANSFER' AND payment_method_id IS NULL AND category_id IS NULL
      AND from_payment_method_id IS NOT NULL AND to_payment_method_id IS NOT NULL)
  ),
  CHECK (from_payment_method_id IS NULL OR to_payment_method_id IS NULL OR from_payment_method_id <> to_payment_method_id)
);

-- Transaction split lines (분할)
CREATE TABLE IF NOT EXISTS tx_line (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  tx_id         uuid NOT NULL REFERENCES tx(id) ON DELETE CASCADE,
  category_id   uuid REFERENCES category(id) ON DELETE SET NULL,
  amount        numeric(14,2) NOT NULL CHECK (amount >= 0),
  memo          text,
  sort_order    int NOT NULL DEFAULT 0,
  created_at    timestamptz NOT NULL DEFAULT now()
);

-- Attachments (영수증/이미지 등)
CREATE TABLE IF NOT EXISTS tx_attachment (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  tx_id       uuid NOT NULL REFERENCES tx(id) ON DELETE CASCADE,
  file_name   text NOT NULL,
  mime_type   text,
  file_size   bigint,
  storage_url text NOT NULL,
  created_at  timestamptz NOT NULL DEFAULT now()
);

-- Transaction <-> Tag (N:M)
CREATE TABLE IF NOT EXISTS tx_tag (
  tx_id   uuid NOT NULL REFERENCES tx(id) ON DELETE CASCADE,
  tag_id  uuid NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
  PRIMARY KEY (tx_id, tag_id)
);

-- Auto categorization rules
CREATE TABLE IF NOT EXISTS auto_rule (
  id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id          uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,
  name              text NOT NULL,
  priority          int NOT NULL DEFAULT 10,
  enabled           boolean NOT NULL DEFAULT true,

  match_field       rule_field NOT NULL,
  match_operator    rule_operator NOT NULL,
  match_value       text NOT NULL,

  payment_method_id uuid REFERENCES payment_method(id) ON DELETE SET NULL,
  min_amount        numeric(14,2),
  max_amount        numeric(14,2),

  set_category_id   uuid REFERENCES category(id) ON DELETE SET NULL,
  set_status        tx_status,
  mark_recurring    boolean NOT NULL DEFAULT false,

  created_at        timestamptz NOT NULL DEFAULT now(),
  updated_at        timestamptz NOT NULL DEFAULT now(),

  CHECK (min_amount IS NULL OR min_amount >= 0),
  CHECK (max_amount IS NULL OR max_amount >= 0),
  CHECK (min_amount IS NULL OR max_amount IS NULL OR min_amount <= max_amount)
);

-- Budgets
CREATE TABLE IF NOT EXISTS budget (
  id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id    uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,
  period_type  budget_period_type NOT NULL DEFAULT 'MONTH',
  period_start date NOT NULL,
  period_end   date NOT NULL,
  rollover     boolean NOT NULL DEFAULT false,
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  CHECK (period_end >= period_start),
  UNIQUE (ledger_id, period_type, period_start)
);

CREATE TABLE IF NOT EXISTS budget_item (
  id                      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  budget_id               uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
  category_id             uuid NOT NULL REFERENCES category(id) ON DELETE RESTRICT,
  amount                  numeric(14,2) NOT NULL CHECK (amount >= 0),
  alert_threshold_percent smallint NOT NULL DEFAULT 80 CHECK (alert_threshold_percent BETWEEN 0 AND 100),
  is_fixed_cost           boolean NOT NULL DEFAULT false,
  created_at              timestamptz NOT NULL DEFAULT now(),
  updated_at              timestamptz NOT NULL DEFAULT now(),
  UNIQUE (budget_id, category_id)
);

-- User settings
CREATE TABLE IF NOT EXISTS user_setting (
  user_id                 uuid PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
  default_ledger_id        uuid REFERENCES ledger(id) ON DELETE SET NULL,

  locale                  text NOT NULL DEFAULT 'ko-KR',
  timezone                text NOT NULL DEFAULT 'Asia/Tokyo',
  currency_code           char(3) NOT NULL DEFAULT 'JPY',

  dark_mode               boolean NOT NULL DEFAULT true,

  notify_budget            boolean NOT NULL DEFAULT true,
  notify_uncategorized     notify_freq NOT NULL DEFAULT 'WEEKLY',
  notify_recurring_detect  boolean NOT NULL DEFAULT true,

  updated_at              timestamptz NOT NULL DEFAULT now()
);

-- Audit log
CREATE TABLE IF NOT EXISTS audit_event (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id      uuid REFERENCES ledger(id) ON DELETE CASCADE,
  actor_user_id  uuid REFERENCES app_user(id) ON DELETE SET NULL,

  action         text NOT NULL,
  entity_type    text NOT NULL,
  entity_id      uuid,
  field_name     text,
  old_value      text,
  new_value      text,

  created_at     timestamptz NOT NULL DEFAULT now()
);

-- Import tracking (CSV 등)
CREATE TABLE IF NOT EXISTS import_batch (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ledger_id    uuid NOT NULL REFERENCES ledger(id) ON DELETE CASCADE,
  source      text NOT NULL,
  file_name   text,
  status      text NOT NULL DEFAULT 'DONE', -- RUNNING/DONE/FAILED
  created_by  uuid REFERENCES app_user(id) ON DELETE SET NULL,
  created_at  timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- 5) Indexes
-- ============================================================

-- ledger_member
CREATE INDEX IF NOT EXISTS idx_ledger_member_user ON ledger_member(user_id);

-- payment_method
CREATE INDEX IF NOT EXISTS idx_payment_method_ledger ON payment_method(ledger_id);

-- category
CREATE INDEX IF NOT EXISTS idx_category_ledger ON category(ledger_id);
CREATE INDEX IF NOT EXISTS idx_category_parent ON category(parent_id);

-- tag
CREATE INDEX IF NOT EXISTS idx_tag_ledger ON tag(ledger_id);

-- tx
CREATE INDEX IF NOT EXISTS idx_tx_ledger_time ON tx(ledger_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_tx_status ON tx(ledger_id, status);
CREATE INDEX IF NOT EXISTS idx_tx_category ON tx(category_id);
CREATE INDEX IF NOT EXISTS idx_tx_payment_method ON tx(payment_method_id);
CREATE INDEX IF NOT EXISTS idx_tx_transfer_from ON tx(from_payment_method_id);
CREATE INDEX IF NOT EXISTS idx_tx_transfer_to ON tx(to_payment_method_id);

-- tx_line / attachment
CREATE INDEX IF NOT EXISTS idx_tx_line_tx ON tx_line(tx_id);
CREATE INDEX IF NOT EXISTS idx_tx_attachment_tx ON tx_attachment(tx_id);

-- auto_rule
CREATE INDEX IF NOT EXISTS idx_auto_rule_ledger_priority ON auto_rule(ledger_id, enabled, priority);

-- budget
CREATE INDEX IF NOT EXISTS idx_budget_ledger_period ON budget(ledger_id, period_start);

-- audit/import
CREATE INDEX IF NOT EXISTS idx_audit_ledger_time ON audit_event(ledger_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_import_batch_ledger_time ON import_batch(ledger_id, created_at DESC);

-- ============================================================
-- 6) Triggers for updated_at
-- ============================================================

DO $$ BEGIN
  CREATE TRIGGER trg_app_user_updated_at
  BEFORE UPDATE ON app_user
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_ledger_updated_at
  BEFORE UPDATE ON ledger
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_payment_method_updated_at
  BEFORE UPDATE ON payment_method
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_category_updated_at
  BEFORE UPDATE ON category
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_tx_updated_at
  BEFORE UPDATE ON tx
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_auto_rule_updated_at
  BEFORE UPDATE ON auto_rule
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_budget_updated_at
  BEFORE UPDATE ON budget
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_budget_item_updated_at
  BEFORE UPDATE ON budget_item
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TRIGGER trg_user_setting_updated_at
  BEFORE UPDATE ON user_setting
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

COMMIT;
