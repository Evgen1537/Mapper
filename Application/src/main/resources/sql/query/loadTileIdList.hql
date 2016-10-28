select t.id from Tile t
where
    t.layer.id = :layerId
    and t.z = :z