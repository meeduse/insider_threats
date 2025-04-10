#!/bin/bash

echo "Compilation du projet..."
mvn clean test-compile
echo -e "\nLancement de 20 runs... Ça prend du temps !\n"

 
extract_or_default() {
    local pattern="$1"
    local line
    line=$(echo "$output" | grep "$pattern" | tail -1)
    if [[ -z "$line" ]]; then
        echo "?"
    else
        echo "$line" | sed 's/.*: //;s/ seconds//'
    fi
}


printf "%-5s | %-10s | %-15s | %-20s | %-20s | %-15s | %-15s\n" \
       "Run" "Score" "Transitions" "Unique_Trans." "States" "Evaluations" "Duration(s)"
echo "---------------------------------------------------------------------------------------------------------------"


for i in $(seq 1 20); do

    output=$(mvn exec:java -Dexec.mainClass="apicalis.Tests_ProB" \
                   -Dexec.classpathScope=test 2>&1 | grep -v "WARNING:")

    score=$(extract_or_default "Best solution")
    transitions=$(extract_or_default "Total number of transitions :")
    uniq_transitions=$(extract_or_default "Total number of transitions (unique)")
    states=$(extract_or_default "Total number of explored states")
    evaluations=$(extract_or_default "Total number of evaluations")
    duration=$(extract_or_default "Total duration (main)")

    printf "%-5s | %-10s | %-15s | %-20s | %-20s | %-15s | %-15s\n" \
           "$i" "$score" "$transitions" "$uniq_transitions" "$states" "$evaluations" "$duration"
done
