# -*- coding:utf-8

import sys
import smtplib
from email.message import Message
from email.mime.text import MIMEText
from email.Header import Header

template = u'''
%s，你好

你所在团队是%s，你加入立思辰研发中心的时间是%s。本次检查周期为2010年11月8日至2010年12月31日，共%s个工作日。

你在SVN上完成了%s次提交，这些提交是在%s天内完成的，其中有%s次提交是没有注释的。

在RDM上你及时提交了%s天的工作日志，与预期的次数差距是%s次。

'''

def all_lines(filepath):
    fileobj = open(filepath)
    for line in fileobj:
        yield unicode(line, 'gbk')
        
for line in all_lines(sys.argv[1]):
    v = line.split(',')
    team = v[0].strip()
    name = v[1].strip()
    join_date = v[2].strip()
    ci_days = v[3].strip()
    ci_total = v[4].strip()
    ci_no_comments = v[5].strip()
    work_days = v[6].strip()
    rdm_days = v[7].strip()
    rdm_diff = v[8].strip()
    email = v[9].strip()
    if (name == u'姓名'):
        continue
    if len(email) == 0:
        continue

    text = template % (name, team, join_date, work_days, ci_total, ci_days, ci_no_comments, rdm_days, rdm_diff)
    
    msg = MIMEText(text.encode('gbk'), 'plain', 'gbk')
    msg.set_charset('gbk')
    msg['subject'] = (Header(u'上季度检查执行情况', 'utf-8'))
    msg['from'] = "hutiejun@lanxum.com"
    msg['to'] = "hutiejun@lanxum.com"
    
    smtp = smtplib.SMTP('mail.lanxum.com')
    smtp.sendmail("hutiejun@lanxum.com", "hutiejun@lanxum.com", msg.as_string())
    print msg.as_string()
    
    break