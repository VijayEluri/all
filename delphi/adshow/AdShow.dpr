program AdShow;

uses
  Forms,
  Main in 'Main.pas' {MainForm},
  ShowScreen in 'ShowScreen.pas' {FormScreen},
  PlayThread in 'PlayThread.pas',
  TopImage in 'TopImage.pas' {FormTopImage: TFCEmbeddedForm},
  TopFlash in 'TopFlash.pas' {FormTopFlash: TFCEmbeddedForm},
  TransitionCell in 'TransitionCell.pas';

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TMainForm, MainForm);
  Application.CreateForm(TFormScreen, FormScreen);
  Application.Run;
end.
