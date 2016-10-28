select l from Layer l
where
    l.orderNumber > :maxOrderNumber
    and l.type in :layerTypes
order by l.orderNumber