select
    1
from markers
where
    layer_id in :layerIdList
    or exit_id in :layerIdList