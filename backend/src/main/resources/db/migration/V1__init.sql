-- Create the "users" table if it does not already exist.
create table if not exists public.users
(
    id         bigserial primary key,
    email      varchar unique not null,
    name       text           not null,
    surname    text           not null,
    address    text           not null,
    created_at timestamptz    not null default now(),
    updated_at timestamptz    not null default now()
);

-- Updater trigger function: sets "updated_at" to NOW() before every UPDATE.
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

-- trigger before update for each row of "public.users", then execute "set_updated_at" function.
create trigger trg_users_updated_at
    before update
    on public.users
    for each row
execute function set_updated_at();
