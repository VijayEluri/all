;Include constants
#include <GUIConstantsEx.au3>

Opt('MustDeclareVars', 1)

_Main()

Func _ProcessFile($file, $pdffile)
	Const $InDesignTitle = "Adobe InDesign CS2"
	WinWait($InDesignTitle)
	WinActivate($InDesignTitle)

	; open the file
	Send("^o")
	WinWaitActive("Open a File")
	ControlSetText("Open a File", "", "[CLASS:Edit; INSTANCE:1]", $file)
	Send("!o")

	; export to pdf
	While 1
		Send("^e")
		if WinExists("Export") Then ExitLoop
		Sleep(500)
	WEnd
	WinWaitActive("Export")
	ControlSetText("Export", "", "[CLASS:Edit; INSTANCE:1]", $pdffile)
	Send("!s")
	
	WinWaitActive("Export Adobe PDF")
	Send("!x")

	WinWaitActive("Generating PDF")
	WinWaitClose("Generating PDF")

	; close the file
	Send("^w")
EndFunc

Func _ProcessDir($dir)
	; Shows the filenames of all files in the current directory.
	Local $search = FileFindFirstFile($dir & "*.*")  

	; Check if the search was successful
	If $search = -1 Then
		MsgBox(0, "Error", "No files/directories matched the search pattern")
		Exit
	EndIf

	Local $fileCount = 1
	
	While 1
		Local $file = FileFindNextFile($search) 
		If @error Then ExitLoop
		
		Local $attr = FileGetAttrib($dir & $file)
		If @error Then ExitLoop
		If StringInStr($attr, "D") Then
			Local $searchIndd = FileFindFirstFile($dir & $file & "\*.indd")
			if $searchIndd <> -1 Then
				Local $fileIndd = FileFindNextFile($searchIndd)
				Local $fileName = $dir & $file & "\" & $fileIndd
				Local $pdfFileName = StringFormat("%d.pdf", $fileCount)
				_ProcessFile($fileName, $pdfFileName)
				$fileCount = $fileCount + 1
			endif
			FileClose($searchIndd)
		EndIf
	WEnd

	; Close the search handle
	FileClose($search)
	MsgBox(0, "Completed", "Completed!")
EndFunc

Func _Main()

	;Initialize variables
	Local $GUIWidth = 300, $GUIHeight = 250
	Local $Edit_1, $OK_Btn, $Cancel_Btn, $msg

	#forceref $Edit_1

	;Create window
	GUICreate("New GUI", $GUIWidth, $GUIHeight)

	;Create an edit box with no text in it
	$Edit_1 = GUICtrlCreateEdit("", 10, 10, 280, 190)
	GUICtrlSetData($Edit_1, "c:\Temp\")

	;Create an "OK" button
	$OK_Btn = GUICtrlCreateButton("OK", 75, 210, 70, 25)

	;Create a "CANCEL" button
	$Cancel_Btn = GUICtrlCreateButton("Close", 165, 210, 70, 25)

	;Show window/Make the window visible
	GUISetState(@SW_SHOW)

	;Loop until:
	;- user presses Esc
	;- user presses Alt+F4
	;- user clicks the close button
	While 1
		;After every loop check if the user clicked something in the GUI window
		$msg = GUIGetMsg()

		Select

			;Check if user clicked on the close button
			Case $msg = $GUI_EVENT_CLOSE
				;Destroy the GUI including the controls
				GUIDelete()
				;Exit the script
				Exit

				;Check if user clicked on the "OK" button
			Case $msg = $OK_Btn
				Local $text = GUICtrlRead($Edit_1)
				_ProcessDir($text)
				;MsgBox(64, "New GUI", $text)

				;Check if user clicked on the "CANCEL" button
			Case $msg = $Cancel_Btn
				;Destroy the GUI including the controls
				GUIDelete()
				;Exit the script
				Exit

		EndSelect

	WEnd
EndFunc   ;==>_Main
