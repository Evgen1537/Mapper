select t from Tile t
left join fetch t.image
where
    t.id = :tileId