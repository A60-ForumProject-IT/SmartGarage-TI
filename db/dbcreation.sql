CREATE DATABASE IF NOT EXISTS smart_garage;
USE smart_garage;

create table avatars
(
    id         int auto_increment
        primary key,
    avatar_url varchar(255) not null
);

create table base_services
(
    Id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table brands
(
    id         int auto_increment
        primary key,
    brand_name varchar(50) not null
);

create table engines
(
    id          int auto_increment
        primary key,
    engine_type varchar(50) not null
);

create table models
(
    id         int auto_increment
        primary key,
    model_name varchar(50) not null
);

create table roles
(
    id        int auto_increment
        primary key,
    role_name varchar(50) not null
);

create table services
(
    id              int auto_increment
        primary key,
    name            varchar(100)         not null,
    price           decimal(10, 2)       not null,
    is_deleted      tinyint(1) default 0 not null,
    base_service_id int                  null,
    constraint fk_base_service
        foreign key (base_service_id) references base_services (Id)
);

create table users
(
    id           int auto_increment
        primary key,
    username     varchar(50)  not null,
    first_name   varchar(50)  null,
    last_name    varchar(50)  null,
    email        varchar(100) not null,
    password     varchar(255) not null,
    role_id      int          null,
    phone_number varchar(10)  not null,
    constraint email
        unique (email),
    constraint username
        unique (username),
    constraint users_pk
        unique (phone_number),
    constraint users_ibfk_1
        foreign key (role_id) references roles (id)
);

create index role_id
    on users (role_id);

create table users_avatars
(
    user_id   int not null,
    avatar_id int not null,
    primary key (user_id, avatar_id),
    constraint users_avatars_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade,
    constraint users_avatars_ibfk_2
        foreign key (avatar_id) references avatars (id)
            on delete cascade
);

create index avatar_id
    on users_avatars (avatar_id);

create table years
(
    id   int auto_increment
        primary key,
    year int not null
);

create table vehicles
(
    id             int auto_increment
        primary key,
    brand_id       int(200)             null,
    model_id       int(200)             null,
    year_id        int(200)             null,
    engine_type_id int(200)             null,
    is_deleted     tinyint(1) default 0 null,
    constraint vehicles_ibfk_1
        foreign key (brand_id) references brands (id),
    constraint vehicles_ibfk_2
        foreign key (model_id) references models (id),
    constraint vehicles_ibfk_3
        foreign key (year_id) references years (id),
    constraint vehicles_ibfk_4
        foreign key (engine_type_id) references engines (id)
);

create table clients_cars
(
    id         int auto_increment
        primary key,
    vin        varchar(50)          not null,
    plate      varchar(20)          not null,
    owner      int                  null,
    vehicle_id int                  null,
    is_deleted tinyint(1) default 0 null,
    constraint plate
        unique (plate),
    constraint vin
        unique (vin),
    constraint clients_cars_ibfk_1
        foreign key (owner) references users (id)
            on delete cascade,
    constraint clients_cars_ibfk_2
        foreign key (vehicle_id) references vehicles (id)
);

create index owner
    on clients_cars (owner);

create index vehicle_id
    on clients_cars (vehicle_id);

create table orders
(
    id            int auto_increment
        primary key,
    client_car_id int         not null,
    status        varchar(50) not null,
    order_date    date        not null,
    constraint orders_ibfk_1
        foreign key (client_car_id) references clients_cars (id)
            on delete cascade
);

create table cars_services
(
    clients_cars_id  int    null,
    service_id       int    null,
    service_date     date   null,
    id               int auto_increment
        primary key,
    calculated_price double null,
    order_id         int    null,
    constraint cars_services_pk_2
        unique (id),
    constraint cars_services_ibfk_1
        foreign key (clients_cars_id) references clients_cars (id)
            on delete cascade,
    constraint cars_services_ibfk_2
        foreign key (service_id) references services (id),
    constraint fk_order_id
        foreign key (order_id) references orders (id)
);

create index clients_cars_id
    on cars_services (clients_cars_id);

create index service_id
    on cars_services (service_id);

create index brand_id
    on vehicles (brand_id);

create index engine_type_id
    on vehicles (engine_type_id);

create index model_id
    on vehicles (model_id);

create index year_id
    on vehicles (year_id);

