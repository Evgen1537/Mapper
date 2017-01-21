CREATE TABLE IF NOT EXISTS images (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	content	BLOB
);

CREATE TABLE IF NOT EXISTS layers (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	type	TEXT,
	x	REAL,
	y	REAL,
	visibility	TEXT,
	order_number integer,
	session_path text
);

CREATE TABLE IF NOT EXISTS marker_icons (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	image_id	INTEGER,
	FOREIGN KEY(image_id) REFERENCES images (id)
);

CREATE TABLE IF NOT EXISTS marker_points (
	id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	marker_id	INTEGER,
	x	REAL,
	y	REAL,
	order_number integer,
	FOREIGN KEY(marker_id) REFERENCES markers (id)
);

CREATE TABLE IF NOT EXISTS markers (
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

CREATE TABLE IF NOT EXISTS tiles (
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

create table IF NOT EXISTS REVINFO (
    id integer not null primary key autoincrement,
    timestamp integer,
    executed integer
);

CREATE TABLE IF NOT EXISTS layers_AUD (
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

CREATE TABLE IF NOT EXISTS tiles_AUD (
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

CREATE TABLE IF NOT EXISTS markers_AUD (
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

CREATE TABLE IF NOT EXISTS marker_points_AUD (
    id integer not null,
    REV integer not null,
    REVTYPE integer,
    order_number integer,
    x real,
    y real,
    marker_id integer,
    primary key (id, REV)
);

CREATE TABLE IF NOT EXISTS tracker_folders (
    id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    session_path text,
    state text
);