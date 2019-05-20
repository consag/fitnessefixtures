--
-- 27-FEB-2016 - Jac. Beekers
-- Based on IBM Support document 'How to store the query history in a DB2 for z/OOS table
-- DocId 27039739
--
call RUN_IDAA_CMD('REMOVE IDAA_QUERY_HIST');
DROP TABLE idaa_query_hist;
call RUN_IDAA_CMD('REMOVE IDAA_QUERY_ACTIVE');
DROP TABLE idaa_query_active;

CREATE TABLE idaa_query_hist
(
measured timestamp not null,
planID INTEGER NOT NULL,
user CHAR(8),
productID CHAR(8),
clientUser CHAR(8),
workstation CHAR(18),
application CHAR(20),
locationName CHAR(16),
connName CHAR(8),
connType CHAR(8),
corrID CHAR(12),
authID CHAR(8),
planName CHAR(8),
accounting VARCHAR(201),
subSystemID CHAR(8),
state CHAR(20),
--submitTimestamp timestamp with timezone,
submitTimestamp timestamp,
waitTimeSec INTEGER,
fetchTimeSec INTEGER,
cpuTimeSec INTEGER,
elapsedTimeSec INTEGER,
priority CHAR(20),
resultRows BIGINT,
resultBytes BIGINT,
errorDescription VARCHAR(256),
task INTEGER,
sqltext VARCHAR(128)
)
--IN DATABASE myDB2Database IN ACCELERATOR IDAASYSTEM01
NOT LOGGED COMPRESS YES IN DATABASE myDB2Database
;
call RUN_IDAA_CMD('ADD IDAA_QUERY_HIST');
call RUN_IDAA_CMD('IDAASYNC IDAA_QUERY_HIST');
call RUN_IDAA_CMD('ACCEL ENABLE IDAA_QUERY_HIST');

create table idaa_query_active like idaa_query_hist
not logged compress yes in database myDB2Database
;
call RUN_IDAA_CMD('ADD IDAA_QUERY_ACTIVE');
call RUN_IDAA_CMD('IDAASYNC IDAA_QUERY_ACTIVE');
call RUN_IDAA_CMD('ACCEL ENABLE IDAA_QUERY_ACTIVE');

--
-- Now the same tables again, but as IDAA-Only
commit;
DROP TABLE idaa_query_hist_aot;
commit;
CREATE TABLE idaa_query_hist_aot
(
measured timestamp,
planID INTEGER,
user CHAR(8),
productID CHAR(8),
clientUser CHAR(8),
workstation CHAR(18),
application CHAR(20),
locationName CHAR(16),
connName CHAR(8),
connType CHAR(8),
corrID CHAR(12),
authID CHAR(8),
planName CHAR(8),
accounting VARCHAR(201),
subSystemID CHAR(8),
state CHAR(20),
submitTimestamp timestamp,
waitTimeSec INTEGER,
fetchTimeSec INTEGER,
cpuTimeSec INTEGER,
elapsedTimeSec INTEGER,
priority CHAR(20),
resultRows BIGINT,
resultBytes BIGINT,
errorDescription VARCHAR(256),
task INTEGER,
sqltext VARCHAR(128)
)
IN DATABASE myDB2Database IN ACCELERATOR IDAASYSTEM01;
commit;

DROP TABLE idaa_query_active_aot;
commit;
CREATE TABLE idaa_query_active_aot
(
measured timestamp,
planID INTEGER,
user CHAR(8),
productID CHAR(8),
clientUser CHAR(8),
workstation CHAR(18),
application CHAR(20),
locationName CHAR(16),
connName CHAR(8),
connType CHAR(8),
corrID CHAR(12),
authID CHAR(8),
planName CHAR(8),
accounting VARCHAR(201),
subSystemID CHAR(8),
state CHAR(20),
submitTimestamp timestamp,
waitTimeSec INTEGER,
fetchTimeSec INTEGER,
cpuTimeSec INTEGER,
elapsedTimeSec INTEGER,
priority CHAR(20),
resultRows BIGINT,
resultBytes BIGINT,
errorDescription VARCHAR(256),
task INTEGER,
sqltext VARCHAR(128)
)
IN DATABASE myDB2Database IN ACCELERATOR IDAASYSTEM01;
commit;

set current query acceleration=ALL;
drop table idaa_query_users_aot;
create table idaa_query_users_aot
(authorizationid char(8)
,group varchar(20)
)
in database myDB2Database in accelerator IDAASYSTEM01
;

truncate table idaa_query_hist;
truncate table idaa_query_active;
commit;
delete from idaa_query_hist_aot;
commit;
delete from idaa_query_active_aot;
commit;


set current query acceleration=all;
create table monitor_time_dim
(
 year integer
,month integer
,day integer
,hour integer
,minute integer
,second integer
,yyyy char(4)
,yyyymm char(7)				-- Format: YYYY-MM
,yyyymmdd char(10)			-- Format: YYYY-MM-DD
,yyyymmddhh char(13)		-- Format: YYYY-MM-DD HH
,yyyymmddhhmi char(16)		-- Format: YYYY-MM-DD HH:MI
,yyyymmddhhmiss char(19)	-- Format: YYYY-MM-DD HH:MI:SS
,yyyy_as_ts timestamp
,yyyymm_as_ts timestamp
,yyyymmdd_as_ts timestamp
,yyyymmddhh_as_ts timestamp
,yyyymmddhhmi_as_ts timestamp
,yyyymmddhhmiss_as_ts timestamp
) in database myDB2Database in accelerator IDAASYSTEM01;

create table monitor_time_dim_db2
(
 year integer
,month integer
,day integer
,hour integer
,minute integer
,second integer
,yyyy char(4)
,yyyymm char(7)				-- Format: YYYY-MM
,yyyymmdd char(10)			-- Format: YYYY-MM-DD
,yyyymmddhh char(13)		-- Format: YYYY-MM-DD HH
,yyyymmddhhmi char(16)		-- Format: YYYY-MM-DD HH:MI
,yyyymmddhhmiss char(19)	-- Format: YYYY-MM-DD HH:MI:SS
,yyyy_as_ts timestamp
,yyyymm_as_ts timestamp
,yyyymmdd_as_ts timestamp
,yyyymmddhh_as_ts timestamp
,yyyymmddhhmi_as_ts timestamp
,yyyymmddhhmiss_as_ts timestamp
) NOT LOGGED COMPRESS YES in database myDB2Database;

call RUN_IDAA_CMD('ADD MONITOR_TIME_DIM_DB2');
call RUN_IDAA_CMD('IDAASYNC MONITOR_TIME_DIM_DB2');
call RUN_IDAA_CMD('ACCEL ENABLE MONITOR_TIME_DIM_DB2');


update monitor_time_dim
set
 yyyy=year
,yyyymm=year||'-'||lpad(month,2,'0')
,yyyymmdd=year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0')
,yyyymmddhh=year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0')||' '||lpad(hour,2,'0')
,yyyymmddhhmi=year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0')||' '||lpad(hour,2,'0')||':'||lpad(minute,2,'0')
,yyyymmddhhmiss=year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0')||' '||lpad(hour,2,'0')||':'||lpad(minute,2,'0')||':'||lpad(second,2,'0')
,yyyy_as_ts=timestamp_format(cast(year as char(4)),'YYYY')
,yyyymm_as_ts=timestamp_format(cast(year||'-'||lpad(month,2,'0') as char(7)),'YYYY-MM')
,yyyymmdd_as_ts=timestamp_format(cast(year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0') as char(10)),'YYYY-MM-DD')
,yyyymmddhh_as_ts=timestamp_format(cast(year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0')||' '||lpad(hour,2,'0') as char(13)),'YYYY-MM-DD HH')
,yyyymmddhhmi_as_ts=timestamp_format(cast(year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0')||' '||lpad(hour,2,'0')||':'||lpad(minute,2,'0') as char(16)),'YYYY-MM-DD HH:MI')
,yyyymmddhhmiss_as_ts=timestamp_format(cast(year||'-'||lpad(month,2,'0')||'-'||lpad(day,2,'0')||' '||lpad(hour,2,'0')||':'||lpad(minute,2,'0')||':'||lpad(second,2,'0') as char(19)),'YYYY-MM-DD HH:MI:SS')
;
