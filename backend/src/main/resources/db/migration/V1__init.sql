create extension if not exists pgcrypto;

create table if not exists public.users
(
    id         uuid primary key        default gen_random_uuid(),
    email      varchar unique not null,
    name       text           not null,
    surname    text           not null,
    address    text           not null,
    created_at timestamptz    not null default now(),
    updated_at timestamptz    not null default now()
);

create or replace function set_updated_at()
    returns trigger
    language plpgsql as
$$
begin
    new.updated_at = now();
    return new;
end;
$$;

drop trigger if exists trg_users_updated_at on public.users;
create trigger trg_users_updated_at
    before update
    on public.users
    for each row
execute function set_updated_at();
