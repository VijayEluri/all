unit ShowScreen;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, OleCtrls, ShockwaveFlashObjects_TLB, teImage, FormCont, teBmpMsk,
  TransEff, ExtCtrls, TransitionCell;

type
  TFormScreen = class(TForm)
    FormContainerTop: TFormContainer;
    FormContainerBottom: TFormContainer;
    procedure FormShow(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure FormDestroy(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure FormKeyUp(Sender: TObject; var Key: Word;
      Shift: TShiftState);
  private
    { Private declarations }
    TopCell: TTransitionCell;
    BottomCell: TTransitionCell;
  public
    { Public declarations }
  end;

var
  FormScreen: TFormScreen;
  TransitionTop: TTransitionEffect;
  TransitionBottom: TTransitionEffect;

implementation

uses PlayThread, TopImage, TopFlash;

{$R *.dfm}

procedure TFormScreen.FormShow(Sender: TObject);
var
  playThread: TPlayThread;
begin
  FormContainerTop.Left := 0;
  FormContainerTop.Top := 0;
  FormContainerTop.Width := Self.ClientWidth;
  FormContainerTop.Height := Self.ClientHeight div 2;

  FormTopImage.Left := 0;
  FormTopImage.Top := 0;
  FormTopImage.Width := FormContainerTop.Width;
  FormTopImage.Height := FormContainerTop.Height;

  FormTopFlash.Left := 0;
  FormTopFlash.Top := 0;
  FormTopFlash.Width := FormContainerTop.Width;
  FormTopFlash.Height := FormContainerTop.Height;

  FormContainerBottom.Left := 0;
  FormContainerBottom.Top := Self.ClientHeight div 2;
  FormContainerBottom.Width := Self.ClientWidth;
  FormContainerBottom.Height := Self.ClientHeight div 2;

  FormBottomImage.Left := 0;
  FormBottomImage.Top := 0;
  FormBottomImage.Width := FormContainerBottom.Width;
  FormBottomImage.Height := FormContainerBottom.Height;

  FormBottomFlash.Left := 0;
  FormBottomFlash.Top := 0;
  FormBottomFlash.Width := FormContainerBottom.Width;
  FormBottomFlash.Height := FormContainerBottom.Height;


  TopCell.FormContainer := FormContainerTop;
  TopCell.FormImage := FormTopImage;
  TopCell.FormFlash := FormTopFlash;
  TopCell.Transition := TransitionTop;
  TopCell.SleepTime := 2000;

  BottomCell.FormContainer := FormContainerBottom;
  BottomCell.FormImage := FormBottomImage;
  BottomCell.FormFlash := FormBottomFlash;
  BottomCell.Transition := TransitionBottom;
  BottomCell.SleepTime := 2000;

  TPlayThread.Create(False, TopCell, BottomCell);

//  Sleep(50);
//  TPlayThread.Create(False, FormContainerBottom, FormBottomImage,
//    FormBottomFlash, 2000, TransitionBottom);
end;

procedure TFormScreen.FormCreate(Sender: TObject);
var
  maskFileName: String;
  transition1: TBmpMaskTransition;
  transition2: TBmpMaskTransition;
begin
  maskFileName := ExtractFilePath(Application.ExeName) + 'mask.bmp';

  transition1 := TBmpMaskTransition.Create(Self);
  transition1.Mask.LoadFromFile(maskFileName);
  transition1.Milliseconds := 2000;
  TransitionTop := transition1;

  transition2 := TBmpMaskTransition.Create(Self);
  transition2.Mask.LoadFromFile(maskFileName);
  transition2.Milliseconds := 2000;
  TransitionBottom := transition2;

  FormTopImage := FormContainerTop.CreateForm(TFormTopImage) as TFormTopImage;
  FormTopFlash := FormContainerTop.CreateForm(TFormTopFlash) as TFormTopFlash;

  FormBottomImage := FormContainerBottom.CreateForm(TFormTopImage) as TFormTopImage;
  FormBottomFlash := FormContainerBottom.CreateForm(TFormTopFlash) as TFormTopFlash;

  TopCell := TTransitionCell.Create;
  BottomCell := TTransitionCell.Create;
end;

procedure TFormScreen.FormDestroy(Sender: TObject);
begin
  if TransitionTop <> nil then TransitionTop.Free;
  FormContainerTop.DestroyForm(FormTopImage);
  FormContainerTop.DestroyForm(FormTopFlash);
  FormContainerBottom.DestroyForm(FormBottomImage);
  FormContainerBottom.DestroyForm(FormBottomFlash);

  TopCell.Free;
  BottomCell.Free;
end;

procedure TFormScreen.FormClose(Sender: TObject; var Action: TCloseAction);
begin
  FormTopFlash.Stop;
  FormBottomFlash.Stop;
end;

procedure TFormScreen.FormKeyUp(Sender: TObject; var Key: Word;
  Shift: TShiftState);
begin
  if Key = VK_ESCAPE then begin
    Application.Terminate;
  end;
end;

end.
