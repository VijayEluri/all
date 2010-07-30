/*
priority:
	0: low
	1: normal
	2: high
	3: urgent
 */
 
drop table task;

create table task (
  task_id INTEGER PRIMARY KEY,
  parent_id INTEGER,
  category_id INTEGER,
  content TEXT,
  note TEXT,
  priority INTEGER,
  completed INTEGER,
  float INTEGER,
  trash INTEGER,
  stared INTEGER,
  due_date TEXT,
  completion_date TEXT,
  repeat_pattern TEXT
);

drop table category;

create table category (
	category_id INTEGER PRIMARY KEY,
	category_name TEXT,
	icon_name
);

delete from task;

insert into task values(1, -1, -1, "todo 1", "note of todo1", 1, 0, 0, 0, 0, "", "", "");
insert into task values(2, -1, -1, "todo 2", "note of todo2", 2, 0, 0, 0, 0, "", "", "");
insert into task values(3, 1, -1, "todo 1.1", "note of todo1.1", 1, 0, 0, 0, 0, "", "", "");
insert into task values(4, 1, -1, "todo 1.2", "note of todo1.2", 3, 0, 0, 0, 0, "", "", "");

select * from task;

delete from category;

insert into category values(1, "Work", "");
insert into category values(2, "Personal", "");

select * from category;
