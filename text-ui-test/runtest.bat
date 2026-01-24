@ECHO OFF

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM delete saved data from previous run (for deterministic tests)
if exist ..\data\aoko.txt del ..\data\aoko.txt

REM compile the code into the bin folder
REM (recursively) gather all .java sources into a file list for javac
if exist sources.txt del sources.txt
dir /s /b ..\src\main\java\*.java > sources.txt
javac -Xlint:none -d ..\bin @sources.txt
IF ERRORLEVEL 1 (
    echo ********** BUILD FAILURE **********
    exit /b 1
)
REM no error here, errorlevel == 0

REM run the program from project root so ./data/aoko.txt resolves correctly
pushd ..
java -classpath bin aoko.Aoko < text-ui-test\input.txt > text-ui-test\ACTUAL.TXT
popd

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT
