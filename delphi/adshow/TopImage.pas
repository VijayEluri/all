unit TopImage;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, FormCont, teImage, JPEG;

type
  TFormTopImage = class(TFCEmbeddedForm)
    TEImage1: TTEImage;
    procedure FCEmbeddedFormCreate(Sender: TObject);
    procedure FCEmbeddedFormDestroy(Sender: TObject);
  private
    jpegImage: TJPEGImage;
  public
    procedure LoadImage(FileNameOnly: String);
  end;

var
  FormTopImage: TFormTopImage;
  FormBottomImage: TFormTopImage;
  
implementation

{$R *.DFM}

{ TFormTopImage }

procedure TFormTopImage.LoadImage(FileNameOnly: String);
var
  baseDir: String;
begin
  baseDir := ExtractFilePath(Application.ExeName);
  jpegImage.LoadFromFile(baseDir + FileNameOnly);
  TEImage1.Picture.Assign(jpegImage);
end;

procedure TFormTopImage.FCEmbeddedFormCreate(Sender: TObject);
begin
  jpegImage := TJPEGImage.Create;
end;

procedure TFormTopImage.FCEmbeddedFormDestroy(Sender: TObject);
begin
  jpegImage.Free;
end;

end.
