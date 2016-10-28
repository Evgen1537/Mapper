select m from Marker m
left join fetch m.layer
left join fetch m.exit
where
    m.essence like :query
    or m.substance like :query
    or m.vitality like :query
    or lower(m.type) like :query