#!/usr/bin/env python

import wx
from wx.stc import *

app = wx.PySimpleApp()
frame = wx.Frame(None, wx.ID_ANY, "Hello World")
frame.Show(True)
app.MainLoop()