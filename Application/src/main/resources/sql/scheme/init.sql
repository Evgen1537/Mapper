CREATE TABLE hibernate_sequence (
	next_val	INTEGER
);

insert into hibernate_sequence (next_val) values (10);

CREATE TABLE images (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	content	BLOB
);

CREATE TABLE layers (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	type	TEXT,
	x	REAL,
	y	REAL,
	visibility	TEXT,
	order_number integer,
	session_path text
);

CREATE TABLE marker_icons (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	image_id	INTEGER,
	FOREIGN KEY(image_id) REFERENCES images (id)
);

CREATE TABLE marker_points (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	marker_id	INTEGER,
	x	REAL,
	y	REAL,
	order_number integer,
	FOREIGN KEY(marker_id) REFERENCES markers (id)
);

CREATE TABLE markers (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	type	TEXT,
	marker_icon_id	INTEGER,
	layer_id	INTEGER,
	exit_id	INTEGER,
	essence TEXT,
    substance TEXT,
    vitality TEXT,
    comment TEXT,
	FOREIGN KEY(marker_icon_id) REFERENCES marker_icons (id),
	FOREIGN KEY(layer_id) REFERENCES layers (id),
	FOREIGN KEY(exit_id) REFERENCES layers (id)
);

CREATE TABLE tiles (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	x	REAL,
	y	REAL,
	z	INTEGER,
	hash TEXT,
	layer_id	INTEGER,
	image_id	INTEGER,
	FOREIGN KEY(layer_id) REFERENCES layers (id),
	FOREIGN KEY(image_id) REFERENCES images (id)
);

create table REVINFO (
    id integer not null primary key autoincrement,
    timestamp integer,
    executed integer
);

CREATE TABLE layers_AUD (
    id integer not null,
    REV integer not null,
    REVTYPE integer,
    name text,
    order_number integer,
    session_path text,
    type text,
    visibility text,
    x real,
    y real,
    primary key (id, REV)
);

CREATE TABLE tiles_AUD (
    id integer not null,
    REV integer not null,
    REVTYPE integer,
    x	REAL,
    y	REAL,
    z	INTEGER,
    hash TEXT,
    layer_id	INTEGER,
    image_id	INTEGER,
    primary key (id, REV)
);

CREATE TABLE markers_AUD (
    id integer not null,
    REV integer not null,
    REVTYPE integer,
    comment text,
    essence text,
    substance text,
    type text,
    vitality text,
    exit_id integer,
    layer_id integer,
    marker_icon_id integer,
    primary key (id, REV)
);

CREATE TABLE marker_points_AUD (
    id integer not null,
    REV integer not null,
    REVTYPE integer,
    order_number integer,
    x real,
    y real,
    marker_id integer,
    primary key (id, REV)
);

insert into REVINFO (id,timestamp,executed) values (null,0,-1);

insert into layers (id,name,type,x,y,visibility,order_number) values (null,"Ground","GROUND",0,0,"FULL",0);
insert into layers_AUD (id,REV,REVTYPE,name,type,x,y,visibility,order_number) values (
    (select max(id) from layers),
    (select id from REVINFO),
    0,"Ground","GROUND",0,0,"FULL",0
);

insert into layers (id,name,type,x,y,visibility,order_number) values (null,"Cave","CAVE",0,0,"FULL",1);
insert into layers_AUD (id,REV,REVTYPE,name,type,x,y,visibility,order_number) values (
    (select max(id) from layers),
    (select id from REVINFO),
    0,"Cave","CAVE",0,0,"FULL",1
);

insert into layers (id,name,type,x,y,visibility,order_number) values (null,"Cave","CAVE",0,0,"FULL",2);
insert into layers_AUD (id,REV,REVTYPE,name,type,x,y,visibility,order_number) values (
    (select max(id) from layers),
    (select id from REVINFO),
    0,"Cave","CAVE",0,0,"FULL",2
);

insert into layers (id,name,type,x,y,visibility,order_number) values (null,"Cave","CAVE",0,0,"FULL",3);
insert into layers_AUD (id,REV,REVTYPE,name,type,x,y,visibility,order_number) values (
    (select max(id) from layers),
    (select id from REVINFO),
    0,"Cave","CAVE",0,0,"FULL",3
);

insert into layers (id,name,type,x,y,visibility,order_number) values (null,"Cave","CAVE",0,0,"FULL",4);
insert into layers_AUD (id,REV,REVTYPE,name,type,x,y,visibility,order_number) values (
    (select max(id) from layers),
    (select id from REVINFO),
    0,"Cave","CAVE",0,0,"FULL",4
);

insert into layers (id,name,type,x,y,visibility,order_number) values (null,"Cave","CAVE",0,0,"FULL",5);
insert into layers_AUD (id,REV,REVTYPE,name,type,x,y,visibility,order_number) values (
    (select max(id) from layers),
    (select id from REVINFO),
    0,"Cave","CAVE",0,0,"FULL",5
);








