create table public_action_tokens (
                                      public_action_token_id uuid primary key,
                                      company_id uuid not null,
                                      token_hash varchar(64) not null unique,
                                      action_type varchar(100) not null,
                                      expires_at timestamp not null,
                                      used boolean not null default false,
                                      used_at timestamp null,
                                      created_at timestamp not null
);

create index idx_public_action_tokens_company_id
    on public_action_tokens(company_id);

create index idx_public_action_tokens_expires_at
    on public_action_tokens(expires_at);
