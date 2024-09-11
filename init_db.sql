create table users (

                       username varchar (20) primary key,
                       password varchar (50) not null,
                       tel varchar (20)
);

create table chat (

                      user1 varchar (20) references users on delete cascade,
                      user2 varchar (20) references users on delete cascade,
                      primary key (user1, user2)

);

create table message (

                         id varchar (50) primary key,
                         content text,
                         time timestamp not null,
                         sender varchar (20) references users on delete cascade,
                         receiver varchar (20) references users on delete cascade,
                         read boolean not null
);


insert into users (username, password, tel) values
('testuser1', '1', ''),
('testuser2', '2', ''),
('testuser3', '3', ''),
('testuser4', '4', ''),
('testuser5', '5', '');