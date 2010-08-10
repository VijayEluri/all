unit Main;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, Mask;

type
  TMainForm = class(TForm)
    Button1: TButton;
    ButtonRunWindow: TButton;
    Label1: TLabel;
    Label2: TLabel;
    ButtonOpenXML: TButton;
    OpenDialogXML: TOpenDialog;
    EditHeight: TEdit;
    ButtonCalcBasedOnWidth: TButton;
    EditWidth: TEdit;
    ButtonCalcBasedOnHeight: TButton;
    Label3: TLabel;
    procedure ButtonOpenXMLClick(Sender: TObject);
    procedure ButtonRunWindowClick(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure ButtonCalcBasedOnWidthClick(Sender: TObject);
    procedure ButtonCalcBasedOnHeightClick(Sender: TObject);
  private
    { Private declarations }
    procedure RunScreen(Width: Integer; Height: Integer);
  public
    { Public declarations }
  end;

var
  MainForm: TMainForm;

implementation

uses ShowScreen;

{$R *.dfm}

procedure TMainForm.RunScreen(Width, Height: Integer);
var
  left, top: Integer;
begin
  left := (Screen.Width - Width) div 2;
  top := (Screen.Height - Height) div 2;
  FormScreen.Left := left;
  FormScreen.Top := top;
  FormScreen.Width := Width;
  FormScreen.Height := Height;
  FormScreen.ShowModal;
end;

procedure TMainForm.ButtonOpenXMLClick(Sender: TObject);
begin
  OpenDialogXML.Execute;
end;

procedure TMainForm.ButtonRunWindowClick(Sender: TObject);
var
  widthText: String;
  widthValue: Integer;
  heightText: String;
  heightValue: Integer;
begin
  widthText := EditWidth.Text;
  heightText := EditHeight.Text;
  widthValue := StrToInt(widthText);
  heightValue := StrToInt(heightText);
  RunScreen(widthValue, heightValue);
end;

procedure TMainForm.Button1Click(Sender: TObject);
var
  width, height: Integer;
begin
  width := Screen.Width;
  height := Screen.Height;
  RunScreen(width, height);
end;

procedure TMainForm.ButtonCalcBasedOnWidthClick(Sender: TObject);
var
  widthText: String;
  widthValue: Integer;
  heightText: String;
  heightValue: Integer;
begin
  widthText := EditWidth.Text;
  widthValue := StrToInt(widthText);
  heightValue := (widthValue * 16) div 9;
  heightText := IntToStr(heightValue);
  EditHeight.Text := heightText;
end;

procedure TMainForm.ButtonCalcBasedOnHeightClick(Sender: TObject);
var
  widthText: String;
  widthValue: Integer;
  heightText: String;
  heightValue: Integer;
begin
  heightText := EditHeight.Text;
  heightValue := StrToInt(heightText);
  widthValue := (heightValue * 9) div 16;
  widthText := IntToStr(widthValue);
  EditWidth.Text := widthText;
end;

end.
