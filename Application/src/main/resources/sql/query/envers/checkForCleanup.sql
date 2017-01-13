select
	id
from REVINFO
where
	executed = 0
	and timestamp < (select max(timestamp) from REVINFO where executed = 1)