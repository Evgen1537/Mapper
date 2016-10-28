select t from Tile t
left join fetch t.image
where
t.layer.id = :layerId
and t.x + t.layer.x >= :x1 and t.x + t.layer.x <= :x2
and t.y + t.layer.y >= :y1 and t.y + t.layer.y <= :y2
and t.z = :z