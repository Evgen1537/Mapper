select
	id
from revinfo
where
	executed = 0
	and timestamp < (select max(timestamp) from revinfo where executed = 1)