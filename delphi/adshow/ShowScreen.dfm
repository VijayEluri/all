object FormScreen: TFormScreen
  Left = 192
  Top = 107
  BorderStyle = bsNone
  Caption = 'FormScreen'
  ClientHeight = 536
  ClientWidth = 971
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  KeyPreview = True
  OldCreateOrder = False
  Position = poDefault
  OnClose = FormClose
  OnCreate = FormCreate
  OnDestroy = FormDestroy
  OnKeyUp = FormKeyUp
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 13
  object FormContainerTop: TFormContainer
    Left = 72
    Top = 48
    Width = 225
    Height = 137
    ParentBackground = False
    TabOrder = 0
  end
  object FormContainerBottom: TFormContainer
    Left = 136
    Top = 228
    Width = 185
    Height = 41
    ParentBackground = False
    TabOrder = 1
  end
end
