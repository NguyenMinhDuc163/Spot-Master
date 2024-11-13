create  database  if not exists  `spot_master_db`

create table users
(
    userId        int auto_increment
        primary key,
    username      varchar(45) not null,
    password      varchar(45) not null,
    score         float       not null,
    win           int         not null,
    draw          int         not null,
    lose          int         not null,
    avgCompetitor float       not null,
    avgTime       float       not null
);

create table images
(
    id           int auto_increment
        primary key,
    image_path_1 varchar(255) not null,
    image_path_2 varchar(255) not null,
    description  text         null
);

create table games
(
    id         int auto_increment
        primary key,
    player_id  int      null,
    start_time datetime null,
    end_time   datetime null,
    score      int      null,
    time_limit int      null,
    constraint games_ibfk_1
        foreign key (player_id) references users (userId)
);

create index player_id
    on games (player_id);
create table differences
(
    id           int auto_increment
        primary key,
    image_id     int   null,
    x_coordinate float null,
    y_coordinate float null,
    constraint differences_ibfk_1
        foreign key (image_id) references images (id)
);

create index image_id
    on differences (image_id);

create table game_differences
(
    id            int auto_increment
        primary key,
    game_id       int      null,
    difference_id int      null,
    found_time    datetime null,
    constraint game_differences_ibfk_1
        foreign key (game_id) references games (id),
    constraint game_differences_ibfk_2
        foreign key (difference_id) references differences (id)
);

create index difference_id
    on game_differences (difference_id);

create index game_id
    on game_differences (game_id);