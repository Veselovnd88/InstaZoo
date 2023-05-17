create table comment
(
    id         bigserial    not null,
    created_at timestamp(6),
    message    text         not null,
    user_id    bigint       not null,
    username   varchar(255) not null,
    post_id    bigint,
    primary key (id)
);


create table image
(
    id          bigserial    not null,
    created_at  timestamp(6),
    image_bytes bytea,
    name        varchar(255) not null,
    post_id     bigint,
    user_id     bigint,
    primary key (id)
);

create table post
(
    id         bigserial not null,
    created_at timestamp(6),
    caption    varchar(255),
    likes      integer,
    location   varchar(255),
    title      varchar(255),
    user_id    bigint,
    primary key (id)
);

create table post_liked_users
(
    post_id     bigint not null,
    liked_users varchar(255)
);

create table user_role
(
    user_id bigint not null,
    roles   smallint
);

create table zoo_user
(
    id         bigserial    not null,
    created_at timestamp(6),
    bio        text,
    email      varchar(255),
    lastname   varchar(255) not null,
    firstname       varchar(255) not null,
    password   varchar(3000),
    username   varchar(255),
    primary key (id)
);

alter table if exists zoo_user
    add constraint user_email_uk unique (email);


alter table if exists zoo_user
    add constraint user_username_uk unique (username);

alter table if exists comment
    add constraint comment_post_fk
        foreign key (post_id)
            references post;

alter table if exists post
    add constraint post_user_id_fk
        foreign key (user_id)
            references zoo_user;

alter table if exists post_liked_users
    add constraint post_liked_users_post_id_fk
        foreign key (post_id)
            references post;

alter table if exists user_role
    add constraint user_role_user_id_fk
        foreign key (user_id)
            references zoo_user;