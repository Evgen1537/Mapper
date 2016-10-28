select
	(ft.x + fl.x) - (st.x + sl.x) x,
    (ft.y + fl.y) - (st.y + sl.y) y
from tiles ft
left join tiles st on ft.hash = st.hash
left join layers fl on ft.layer_id = fl.id
left join layers sl on st.layer_id = sl.id
where
	    ft.layer_id = :firstLayerId  and ft.z = 'Z1'
	and st.layer_id = :secondLayerId and st.z = 'Z1'
	and ft.hash not in (
	    '27166B2B1B679C54E00649A56A574BEB'
	)
group by
	ft.layer_id,
	(ft.x + fl.x) - (st.x + sl.x),
	(ft.y + fl.y) - (st.y + sl.y)
order by
	count(*)
limit 1