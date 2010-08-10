unit PlayThread;

interface

uses
  Classes, SysUtils, Forms, Jpeg, DateUtils, TopImage, TopFlash, FormCont,
  TransEff, TransitionCell;

type

  TPlayThread = class(TThread)
  private
    TopCell: TTransitionCell;
    BottomCell: TTransitionCell;

    procedure ProcessMessageSleep(ms: Integer);
    procedure ShowImage(cell: TTransitionCell; fileName: String);
    procedure TransImageToImage(cell: TTransitionCell; fileName: String);
    procedure TransImageToFlash(cell: TTransitionCell; fileName: String);
    procedure TransFlashToFlash(cell: TTransitionCell; fileName: String);
    procedure TransFlashToImage(cell: TTransitionCell; fileName: String);
  protected
    procedure Execute; override;
  public
    constructor Create(CreateSuspended: Boolean; cellTop: TTransitionCell; cellBottom: TTransitionCell);
    destructor Destroy; override;
  end;

implementation

{ TPlayThread }

constructor TPlayThread.Create(CreateSuspended: Boolean;
  cellTop: TTransitionCell; cellBottom: TTransitionCell);
begin
  self.TopCell := cellTop;
  self.BottomCell := cellBottom;
  inherited Create(CreateSuspended);
end;

destructor TPlayThread.Destroy;
begin
  inherited;
end;

procedure TPlayThread.Execute;
begin
  ShowImage(TopCell, '1.jpg');
  ShowImage(BottomCell, '2.jpg');
  ProcessMessageSleep(TopCell.SleepTime);

  while true do begin
    TransImageToImage(TopCell, '2.jpg');
    TransImageToImage(BottomCell, '3.jpg');
    ProcessMessageSleep(TopCell.SleepTime);

    TransImageToFlash(TopCell, '3.swf');
    ProcessMessageSleep(TopCell.SleepTime);

    TransImageToImage(BottomCell, '4.jpg');
    TransFlashToFlash(TopCell, '4.swf');
    ProcessMessageSleep(TopCell.SleepTime);

    TransFlashToImage(TopCell, '4.jpg');
    TransImageToFlash(BottomCell, '5.swf');
    ProcessMessageSleep(TopCell.SleepTime);
    TransFlashToImage(BottomCell, '3.jpg');
  end;

end;

procedure TPlayThread.ProcessMessageSleep(ms: Integer);
var
  startTime: TDateTime;
begin
  startTime := GetTime;
  while true do begin
    if MilliSecondsBetween(GetTime, startTime) >= ms then Exit;
    Application.ProcessMessages;
  end;
end;

procedure TPlayThread.ShowImage(cell: TTransitionCell; fileName: String);
begin
  with cell do begin
    FormImage.LoadImage(fileName);
    FormContainer.ShowFormEx(FormImage, False);
  end;
end;

procedure TPlayThread.TransFlashToFlash(cell: TTransitionCell; fileName: String);
begin
  with cell do begin
    FormFlash.Stop;
    Transition.Prepare(FormContainer, FormContainer.ClientRect);
    FormFlash.LoadMovie(fileName);
    Transition.Execute;
    Transition.UnPrepare;
    FormFlash.Play;
  end;
end;

procedure TPlayThread.TransFlashToImage(cell: TTransitionCell; fileName: String);
begin
  with cell do begin
    FormFlash.Stop;
    FormImage.LoadImage(fileName);
    FormContainer.ShowFormEx(FormImage, False, Transition);
  end;
end;

procedure TPlayThread.TransImageToFlash(cell: TTransitionCell; fileName: String);
begin
  with cell do begin
    FormFlash.LoadMovie(fileName);
    FormContainer.ShowFormEx(FormFlash, False, Transition);
    FormFlash.Play;
  end;
end;

procedure TPlayThread.TransImageToImage(cell: TTransitionCell; fileName: String);
begin
  with cell do begin
    Transition.Prepare(FormContainer, FormContainer.ClientRect);
    FormImage.LoadImage(fileName);
    Transition.Execute;
    Transition.UnPrepare;
  end;
end;

end.
