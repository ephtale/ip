#!/usr/bin/env bash

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]
then
    mkdir ../bin
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# delete saved data from previous run (for deterministic tests)
if [ -e "../data/aoko.txt" ]
then
    rm ../data/aoko.txt
fi

# compile the code into the bin folder, terminates if error occurred
# exclude JavaFX GUI sources so the text UI tests can run on JDKs without JavaFX
if ! javac -Xlint:none -d ../bin $(find ../src/main/java -path "*/gui/*" -prune -o -name "*.java" -print)
then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# run the program from project root so ./data/aoko.txt resolves correctly
(cd .. && java -classpath bin aoko.Aoko < text-ui-test/input.txt > text-ui-test/ACTUAL.TXT)

# convert to UNIX format
cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT

# compare the output to the expected output
diff ACTUAL.TXT EXPECTED-UNIX.TXT
if [ $? -eq 0 ]
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi