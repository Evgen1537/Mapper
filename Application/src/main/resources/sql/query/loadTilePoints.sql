select
    x,y
from tiles
where
    z = :z
    and layer_id = :layerId
    and x >= :x1
    and y >= :y1
    and x < :x2
    and y < :y2