delete from tiles_AUD where REV in (select id from REVINFO where executed = 0);

delete from layers_AUD where REV in (select id from REVINFO where executed = 0);

delete from markers_AUD where REV in (select id from REVINFO where executed = 0);

delete from marker_points_AUD where REV in (select id from REVINFO where executed = 0);

delete from images where id in (
    select
        i.id
    from images i
    left join tiles t on i.id = t.image_id
    left join marker_icons mi on i.id = mi.image_id
    where
        t.id is null
        and mi.id is null
);

delete from REVINFO where executed = 0