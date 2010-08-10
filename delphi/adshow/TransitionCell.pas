unit TransitionCell;

interface

uses
  FormCont, TransEff, TopFlash, TopImage;

type

  TTransitionCell = class(TObject)
  public
    FormContainer: TFormContainer;
    FormImage: TFormTopImage;
    FormFlash: TFormTopFlash;
    SleepTime: Integer;
    Transition: TTransitionEffect;
  end;

implementation

end.
