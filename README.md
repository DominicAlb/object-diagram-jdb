# object-diagram-jdb
Uses jdb (Java Debugger) to run and analyze code in order to create an object diagram for every step (executed line) in a simple java file.
This program is just a small school project that I did in my spare time. It's most likely not efficient and certainly won't work in every case.

Programmed and tested with JAVA SE 18.0.1 and jdb 18.0 on Windows 11 Pro

Needs:
  - uses Java text blocks, therefore at least Java SE 13
  - jdb

How it works (step by step):
  - Creates a JDB process via ProcessBuilder
  - The analyzer goes through the main file and filters for initialized variables
    - Analyzes the other classes in the package if complex data types were used
    - Creates schemes from the information
  - The jdb process runs every line and the schemes are filled out with the required information
  - After running the program, the completed schemes are evaluated and everything is temporarily stored in an xml file for the sake of simplicity
  - The XML file is then processed into an HTML file or a dot graph (dot graph not tested yet)

Final product:
  1. HTML file with object diagram(s)
  2. XML file with all the information
