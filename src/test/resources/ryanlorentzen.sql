CREATE TABLE ryanlorentzen.foo (
       foo_id INT NOT NULL
     , name TEXT
     , PRIMARY KEY (foo_id)
);

CREATE TABLE ryanlorentzen.bar (
       bar INT NOT NULL
     , name TEXT
     , PRIMARY KEY (bar)
);

CREATE TABLE ryanlorentzen.tblaccess (
       PKId INT NOT NULL
     , strAccess TEXT
     , PRIMARY KEY (PKId)
);

CREATE TABLE ryanlorentzen.tblMemberAccess (
       PKId INT NOT NULL
     , intFkAccessId INT
     , dateAdd TIME(10) WITH TIME ZONE
     , PRIMARY KEY (PKId)
     , CONSTRAINT FK_tblMemberAccess_1 FOREIGN KEY (intFkAccessId)
                  REFERENCES ryanlorentzen.tblaccess (PKId)
);

CREATE TABLE ryanlorentzen.foobar (
       foo_id INT NOT NULL
     , bar INT NOT NULL
     , PRIMARY KEY (foo_id, bar)
     , CONSTRAINT FK_TABLE_5_1 FOREIGN KEY (foo_id)
                  REFERENCES ryanlorentzen.foo (foo_id)
     , CONSTRAINT FK_TABLE_5_2 FOREIGN KEY (bar)
                  REFERENCES ryanlorentzen.bar (bar)
);

