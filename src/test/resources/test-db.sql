create database testProject1;
create database testProject2;

use testProject1;
create table users
(
	id integer
		constraint users_pk
			primary key autoincrement,
	email varchar(40),
	first_name varchar(20) not null,
	last_name varchar(20) not null,
	created_at timestamp not null,
	updated_at timestamp not null,
	deleted_at timestamp
);

create unique index users_email_uindex
	on users (email);

create index trashcan
    on users (deleted_at);

create table customers
(
	id bigint unsigned auto_increment
		primary key,
	billable_id bigint unsigned not null,
	billable_type varchar(255) not null,
	trial_ends_at timestamp null,
	created_at timestamp null,
	updated_at timestamp null
);

create index customers_billable_id_billable_type_index
	on customers (billable_id, billable_type);

use testProject2;

create table failed_jobs
(
	id bigint unsigned auto_increment
		primary key,
	connection text not null,
	queue text not null,
	payload longtext not null,
	exception longtext not null,
	failed_at timestamp default CURRENT_TIMESTAMP not null
);

create table migrations
(
	id int unsigned auto_increment
		primary key,
	migration varchar(255) not null,
	batch int not null
);
