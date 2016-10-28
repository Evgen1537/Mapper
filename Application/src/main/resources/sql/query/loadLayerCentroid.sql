select
    avg(t.x) + l.x,
    avg(t.y) + l.y
from layers l
left join tiles t on l.id = t.layer_id
where
    t.z = 'Z1'
    and l.id = :layerId