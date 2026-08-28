
    create table account (
        default_account bit not null,
        created datetime(6),
        modified datetime(6),
        client_secret varchar(512) not null,
        account_key varchar(255) not null,
        client_id varchar(255) not null,
        comfact_account_id varchar(255),
        description varchar(255),
        id varchar(255) not null,
        municipality_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create index idx_account_municipality_id 
       on account (municipality_id);

    alter table if exists account 
       add constraint uq_account_municipality_id_account_key unique (municipality_id, account_key);
