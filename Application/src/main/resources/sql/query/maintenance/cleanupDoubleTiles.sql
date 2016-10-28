delete from tiles where id in (
	select max(id)
	from tiles
	where z = 'Z1'
	group by x,y,layer_id
	having count(*) > 1
)