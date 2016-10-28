select m
from Marker m
left join fetch m.layer
left join fetch m.exit
where
    exists (
        from MarkerPoint mp
        where m.id = mp.marker.id
        and mp.x >= :x1 and mp.x <= :x2
        and mp.y >= :y1 and mp.y <= :y2
    )