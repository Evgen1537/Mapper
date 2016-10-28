select t from LiteTile t
where
    t.layer.id = :layerId
    and t.z = :z