# PST-Contacts2CSV
Extract the contacts of incoming/outgoing e-mails into *.csv file from a *.PST file.

## Usage
You are free to download the sourcecode and compile it yourself or download the executable from the [release page]

``` java jar PST-Contacts2CSV.jar <PST-File>```

Optionally, you can specify the amount of memory to be used by the JVM:

``` java -Xmx1024m -jar PST-Contacts2CSV.jar <PST-File>```

## Credits
This project uses the following libraries:
* [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) by Apache Foundation.
* [Opencsv](http://opencsv.sourceforge.net/) by OpenCSV project team and collaborators.
* [libpst](https://github.com/rjohnsondev/java-libpst) by Richard Johnson.

## License
* This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) (compatible with GPL v3).