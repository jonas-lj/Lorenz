# Lorenz

This code was used for composing "Lorenz-6674089274190705457 (Seltsamer Attraktor)" by Morten Bach & Jonas Lindstrøm from the album [pieces of infinity 01](https://promo.theorchard.com/0VNkK7jSZSr7CQNGq4Ny?fbclid=IwAR396l5asHEt6SK-2kbKJ5-bzyQGoTGuIGytP26PBcd9UwPyqJs48Lf92Zg). It computes curves in a Lorenz system and maps points of fixed time differences to notes according to a prespecified rule. The resulting tracks are saved as MIDI files.

The program may be built and run using Maven by executing `mvn install` to build and then `mvn exec_java`to run. This will create three MIDI files in the project directory. To change the parameters used for creating the files, change the corresponding values in the file lorenz.properties.

The MOSEF library is included as a submodule since this code depends on it. You may also clone it directly from [GitHub](https://github.com/jonas-lj/MOSEF).

![A curve in a Lorenz system with circles indicating points mapped to notes](https://raw.githubusercontent.com/jonas-lj/Lorenz/main/lorenz.png)
