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