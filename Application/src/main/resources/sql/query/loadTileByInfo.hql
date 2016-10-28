select t from Tile t
left join fetch t.image
where
t.layer.id = :layerId
and t.x = :x
and t.y = :y
and t.z = :z