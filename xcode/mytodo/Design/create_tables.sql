/*
priority:
	1: low
	2: normal
	3: high
 */
 
drop table task;

create table task (
  task_id INTEGER PRIMARY KEY,
  parent_id INTEGER,
  task_content TEXT,
  priority INTEGER,
  completed INTEGER,
  float INTEGER,
  due_date TEXT,
  completion_date TEXT,
  repeat_pattern TEXT
);

delete from task;

insert into task values(1, -1, "todo 1", 1, 0, 0, "", "", "");
insert into task values(2, -1, "todo 2", 2, 0, 0, "", "", "");
insert into task values(3, 1, "todo 1-1", 1, 0, 0, "", "", "");
insert into task values(4, 1, "todo 1-2", 3, 0, 0, "", "", "");

select * from task;
