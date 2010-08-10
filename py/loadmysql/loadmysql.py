import MySQLdb
import logging
import datetime
import random

DBNAME = "largedb"
TABLENAME = "largetable"
LOG_FILENAME = "loadmysql_" + datetime.datetime.now().strftime("%Y_%m_%d_%H_%M_%S") + ".log"
COL_BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
global_colid = 1

logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s, %(levelname)s, %(message)s',
                    filename=LOG_FILENAME,
                    filemode='w')
console = logging.StreamHandler()
console.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s: %(levelname)-8s %(message)s')
console.setFormatter(formatter)
logging.getLogger('').addHandler(console)

def execute_sql(sql, conn):
    conn.query(sql)

def list_dbs(conn):
    conn.query("""show databases""")
    rs = conn.store_result()
    rows = rs.fetch_row(0)
    return rows

def has_db(db_to_find, dbs):
    for row in dbs:
        for col in row:
            if col.lower() == db_to_find.lower():
                return 1
    return 0

# create db and swith to it
def create_db(db_to_create, conn):
    str = "create database " + db_to_create
    execute_sql(str, conn)
    str = "use " + db_to_create
    execute_sql(str, conn)

def drop_db(db_to_drop, conn):
    str = "drop database " + db_to_drop
    execute_sql(str, conn)

def create_table(conn):
    str = "CREATE TABLE " + TABLENAME + " (_internal_id INT NOT NULL, col1 VARCHAR(100), col2 VARCHAR(100), col3 INT, col4 DATETIME, col5 VARCHAR(100), col6 VARCHAR(100), col7 VARCHAR(100), col8 VARCHAR(100)) ENGINE=MyISAM"
    logging.debug(str)
    execute_sql(str, conn)

def generate_garbage():
    str = ""
    for i in range(1, 91):
        str = str + random.choice(COL_BASE)
    str = str + ","
    return str


def insert_data(x, y, conn):
    global global_colid
    col1 = generate_garbage() + "col1," + x.__str__() + "," + y.__str__()
    col2 = generate_garbage() + "col2," + x.__str__() + "," + y.__str__()
    col3 = (x * y).__str__()
    col4 = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    col5 = generate_garbage() + "col5," + x.__str__() + "," + y.__str__()
    col6 = generate_garbage() + "col6," + x.__str__() + "," + y.__str__()
    col7 = generate_garbage() + "col7," + x.__str__() + "," + y.__str__()
    col8 = generate_garbage() + "col8," + x.__str__() + "," + y.__str__()
    str = "INSERT INTO " + TABLENAME + " VALUES (" + \
        global_colid.__str__() + ", '" + \
        col1 + "', '" + \
        col2 + "', " + \
        col3 + ", '" + \
        col4 + "', '" + \
        col5 + "', '" + \
        col6 + "', '" + \
        col7 + "', '" + \
        col8 + "')"
    execute_sql(str, conn)
    global_colid = global_colid + 1

conn = MySQLdb.connect (host= "localhost", user = "root", passwd = "", db = "test")
dbs = list_dbs(conn)
if has_db(DBNAME, dbs):
    logging.info("DB exists, dropping ...")
    drop_db(DBNAME, conn)

logging.info("Creating DB ...")
create_db(DBNAME, conn)

logging.info("Creating table ...")
create_table(conn)

logging.info("Start to load data ...")
logging.debug(COL_BASE)

# 1000x1000 loop, total 1,000,000
conn.autocommit(0)
for x in range(1, 1001):
    for y in range(1, 1001):
        insert_data(x, y, conn)
        if y % 1000 == 0:
            logging.info("Commiting... " + x.__str__() + ", " + y.__str__())
            conn.commit()

conn.close ()
