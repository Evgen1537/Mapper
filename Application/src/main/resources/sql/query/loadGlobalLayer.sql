select
    l.id,
    l.name,
    l.visibility,
    l.type,
    count(t.id) tileCount
from layers l
left join tiles t on l.id = t.layer_id and t.z = "Z1"
where
    l.type in ("GROUND","CAVE")
group by
    l.id,
    l.name,
    l.visibility,
    l.type
order by
    l.order_number