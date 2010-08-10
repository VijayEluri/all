unit TopFlash;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, FormCont, OleCtrls, ShockwaveFlashObjects_TLB;

type
  TFormTopFlash = class(TFCEmbeddedForm)
    ShockwaveFlash1: TShockwaveFlash;
  private
  public
    procedure LoadMovie(movie: String);
    procedure Play;
    procedure Stop;
  end;

var
  FormTopFlash: TFormTopFlash;
  FormBottomFlash: TFormTopFlash;

implementation

{$R *.DFM}

{ TFormTopFlash }

procedure TFormTopFlash.LoadMovie(movie: String);
var
  baseDir: String;
begin
  baseDir := ExtractFilePath(Application.ExeName);
  ShockwaveFlash1.Movie := baseDir + movie;
end;

procedure TFormTopFlash.Play;
begin
  ShockwaveFlash1.Play;
end;

procedure TFormTopFlash.Stop;
begin
  ShockwaveFlash1.Stop;
end;

end.
