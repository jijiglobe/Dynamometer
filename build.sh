javac -d main/classes main/src/*.java
javac -d modeling/classes modeling/src/*.java -cp "main/classes"
javac -d analysis/classes analysis/src/*.java -cp "lib/ejml-v0.38-libs/*:lib/jfreechart-1.0.19/lib/*"
