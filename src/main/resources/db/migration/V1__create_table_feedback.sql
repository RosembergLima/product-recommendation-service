create table feedback (
    id uuid primary key,
    user_id varchar(255) not null,
    product_id varchar(50) not null,
    feedback varchar(255) not null,
    comment varchar(255),
    created_at timestamp default now()
)