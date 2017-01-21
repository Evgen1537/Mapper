create table if not exists tracker_folders (
    id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    session_path text,
    state text
);