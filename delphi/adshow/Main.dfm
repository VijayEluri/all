object MainForm: TMainForm
  Left = 439
  Top = 208
  BorderStyle = bsDialog
  Caption = 'Ad Show'
  ClientHeight = 251
  ClientWidth = 290
  Color = clBtnFace
  Font.Charset = ANSI_CHARSET
  Font.Color = clWindowText
  Font.Height = -13
  Font.Name = #26032#23435#20307
  Font.Style = []
  OldCreateOrder = False
  Position = poDesktopCenter
  PixelsPerInch = 96
  TextHeight = 13
  object Label1: TLabel
    Left = 16
    Top = 148
    Width = 42
    Height = 13
    Caption = #39640#24230#65306
  end
  object Label2: TLabel
    Left = 16
    Top = 185
    Width = 42
    Height = 13
    Caption = #23485#24230#65306
  end
  object Label3: TLabel
    Left = 16
    Top = 220
    Width = 259
    Height = 13
    Caption = #22312#27809#26377#36816#34892#20999#25442#29305#25216#30340#26102#20505#25353'ESC'#36864#20986#31243#24207
  end
  object Button1: TButton
    Left = 16
    Top = 56
    Width = 257
    Height = 33
    Caption = #20840#23631#36816#34892
    TabOrder = 0
    OnClick = Button1Click
  end
  object ButtonRunWindow: TButton
    Left = 16
    Top = 96
    Width = 257
    Height = 33
    Caption = #25353#35774#23450#20998#36776#29575#36816#34892
    TabOrder = 1
    OnClick = ButtonRunWindowClick
  end
  object ButtonOpenXML: TButton
    Left = 16
    Top = 16
    Width = 257
    Height = 33
    Caption = #25171#24320'XML'#33410#30446#21333
    TabOrder = 2
    OnClick = ButtonOpenXMLClick
  end
  object EditHeight: TEdit
    Left = 64
    Top = 144
    Width = 97
    Height = 21
    TabOrder = 3
    Text = '800'
  end
  object ButtonCalcBasedOnWidth: TButton
    Left = 168
    Top = 144
    Width = 105
    Height = 21
    Caption = #26681#25454#23485#24230#35745#31639
    TabOrder = 4
    OnClick = ButtonCalcBasedOnWidthClick
  end
  object EditWidth: TEdit
    Left = 64
    Top = 180
    Width = 97
    Height = 21
    TabOrder = 5
    Text = '450'
  end
  object ButtonCalcBasedOnHeight: TButton
    Left = 168
    Top = 180
    Width = 105
    Height = 21
    Caption = #26681#25454#39640#24230#35745#31639
    TabOrder = 6
    OnClick = ButtonCalcBasedOnHeightClick
  end
  object OpenDialogXML: TOpenDialog
    Filter = #33410#30446#21333'|*.XML'
    Left = 264
  end
end
