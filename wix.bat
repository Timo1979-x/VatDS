@echo off
set WIX=C:\Program Files (x86)\WiX Toolset v3.10\bin
set wixPrjDir=%cd%\target
echo %wixPrjDir%
mkdir "%wixPrjDir%\temp"

rem FOR %%D IN (lib,win32,win64) DO "%WIX%\heat.exe" dir "%wixPrjDir%\%%D" -dr PROGRAMDIR -cg compGroup%%D -ag -sfrag -var var.dir%%D -out "%cd%\src\main\wix\%%D.wxs"
rem "%WIX%\candle.exe" -ddirwin32="%wixPrjDir%\win32" -ddirwin64="%wixPrjDir%\win64" -ddirlib="%wixPrjDir%\lib" -nologo "src\main\wix\*.wxs" -out target\temp\
FOR %%D IN (lib,win32,win64) DO "%WIX%\heat.exe" dir "%wixPrjDir%\%%D" -dr PROGRAMDIR -cg compGroup%%D -ag -sfrag -var var.dir%%D -out "target\temp\%%D.wxs"
"%WIX%\candle.exe" -ddirwin32="%wixPrjDir%\win32" -ddirwin64="%wixPrjDir%\win64" -ddirlib="%wixPrjDir%\lib" -nologo "src\main\wix\*.wxs" "target\temp\*.wxs" -out target\temp\
"%WIX%\light.exe" -nologo -out "target\btoReportNG.msi" "target\temp\*.wixobj"
del target\btoReportNG.wixpdb
