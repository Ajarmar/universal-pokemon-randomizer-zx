1. HOW TO USE

Just double click launcher.jar and it will start the randomizer program.
DO NOT change the name of the randomizer program as this will cause the launcher to fail.
The launcher program and the randomizer program must be in the same folder.



2. TECHNICAL DETAILS

The only thing the launcher does is run the following command:

java -Xmx4096M -jar PokeRandoStar.jar

This starts the randomizer application with a maximum Java heap size of 4 gigabytes. This is necessary for being able to randomize the 3DS games.



3. TROUBLESHOOTING

When running the launcher, output will be redirected to a log file named "launcher-log.txt". It may be of interest to look at if you have issues running the launcher.

Some potential issues that could occur:

- You don't have java in your PATH environment variable. Try googling "java path environment variable" + your operating system to find out how to set it. (The log will probably say something like "java is not recognized as a command..".)
- You can't set the heap space to 4 gigabytes. This may occur if you have a 32-bit version of Java. Consider updating to a 64-bit version. (The log will probably say something like "Invalid maximum heap size..")
- The launcher can't execute its command due to operating system differences (shouldn't occur on Windows). Try running "java -Xmx4096M -jar PokeRandoStar.jar" directly from the command line instead.