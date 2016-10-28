select l from Layer l
where
    l.orderNumber < :minOrderNumber
    and l.type in :layerTypes
order by l.orderNumber desc