# Delete entries where the group for it's groupid no longer exists

## The MSSQL script which lists the tables where there is groupid which Group was deleted somehow

### The SQL

```SQL
Use uea_after

declare @table_name varchar(50)
declare @count1 int
declare @sql1 nvarchar(4000)
declare @count2 int
declare @sql2 nvarchar(4000)
declare @params nvarchar(4000)
declare @where nvarchar(1000)
declare @sql3 nvarchar(4000)
declare @params3 nvarchar(4000)
declare @out3 nvarchar(4000)

declare tables cursor for 
	Select TABLE_NAME From INFORMATION_SCHEMA.COLUMNS Where column_name = 'groupId' order by TABLE_NAME;

select @where = N' WHERE groupId NOT IN (SELECT DISTINCT groupId FROM [Group_])'
select @params = N'@cnt int OUTPUT'
select @params3 = N'@s nvarchar(4000) OUTPUT'

open tables
fetch next from tables into @table_name

while @@FETCH_STATUS = 0
begin
	-- count of missing groupids
   select @sql1 = N'SELECT @cnt = COUNT([groupId]) FROM ' +
				 quotename(@table_name) + @where
				 
	-- count distinct of missing groupids
	select @sql2 = N'SELECT @cnt = COUNT(distinct [groupId]) FROM ' +
				 quotename(@table_name) + @where

	-- writing the missing groupids in one line separated by comma
	select @sql3 = N'SET NOCOUNT ON;' +
					N'DECLARE @groupids TABLE(groupId bigint NULL);INSERT INTO @groupids ' +
					N'SELECT distinct [groupId] FROM ' + quotename(@table_name) + @where + '; ' +
					N'select  @s = (select convert(varchar(10), groupId) + ' + ''',''' + 
					' as ''data()'' from @groupids order by groupid for xml path(''''))'

	exec sp_executesql @sql1, @params, @cnt = @count1 OUTPUT

	if @count1 > 0
	begin
		exec sp_executesql @sql2, @params, @cnt = @count2 OUTPUT
		exec sp_executesql @sql3, @params3, @s = @out3 OUTPUT
		-- |TABLE NAME|COUNT MISSINg GRIDS|COUNT DISTINCT MISSING GRIDS|MISSING GRIDS
		print @table_name + '|' + convert(varchar(10), @count1) + '|' + convert(varchar(10), @count2) + '|' + @out3
	end

   fetch next from tables into @table_name	
end

close tables
deallocate tables
```

### The output

```
AssetEntry|4838|40|10648, 10988, 15267, 16086, 16129, 16151, 43679, 51040, 78021, 86317, 95351, 101394, 135964, 143628, 145211, 148956, 153957, 165890, 168570, 178813, 183156, 231469, 231489, 243980, 301599, 311379, 314284, 505234, 982188, 1001111, 1001269, 1061102, 1095198, 1393081, 1419580, 1555200, 3084030, 3306541, 3998811, 4741899,
AssetTag|43|7|86317, 95351, 101394, 168570, 183156, 1001111, 1001269,
AssetVocabulary|10|8|10988, 51040, 78021, 95351, 178813, 178819, 311379, 982188,
DLFileEntryType|1|1|0,
DLFileRank|6|3|10988, 51040, 86317,
DLFolder|7|1|982188,
JournalArticleResource|10|3|243980, 311379, 982188,
JournalContentSearch|4|1|982188,
KaleoAction|2|1|0,
KaleoDefinition|1|1|0,
KaleoNode|4|1|0,
KaleoNotification|3|1|0,
KaleoNotificationRecipient|1|1|0,
KaleoTask|2|1|0,
KaleoTaskAssignment|9|1|0,
KaleoTransition|4|1|0,
Layout|10|1|982188,
LayoutFriendlyURL|10|1|982188,
LayoutSet|4|2|982188, 999117,
MBDiscussion|80|8|0, 10988, 51040, 78021, 86317, 95351, 101394, 982188,
MBMessage|118|10|10648, 10988, 16151, 51040, 78021, 86317, 95351, 101394, 183156, 982188,
MBThread|118|10|10648, 10988, 16151, 51040, 78021, 86317, 95351, 101394, 183156, 982188,
SocialActivity|59|3|16129, 153957, 301599,
```

The first column is the name of table, the second is the number of orphaned groups, the third is the number of dictinct orpahned groups
The last is the groupids separated with comma.


