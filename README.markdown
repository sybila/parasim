# Parasim
Parasim is a tool for robustness analysis. For information not covered in this README visit the [Parasim wiki](https://github.com/sybila/parasim/wiki).

## Requirements

* Runtime: Java (7+), octave (simulation framework is based on octave)
* Building: gradle (available through wrapper)

## Running Parasim

You can download latest Parasim prebuilt as a zip archive in the release section [LINK NEEDED]. Then use bin/parasim (Unix) or bin/parasim.bat (Windows) to execute Parasim.

Note: In the latest version of Parasim, all distributed components have been deprecated. However, the shared memory parallelisation should be still usable.

## Parasim options

Parasim can be executed either in GUI mode (no command line arguments), or in command line mode (argument -t or --terminal).

### Command Line Options
* `-v`, `--version` Print parasim version and exits.
* `-h`, `--help` Print command line (condensed) options and exits.

* `-c <file>`, `--confing <file>` Specify parasim configuration file (other than default).
* `-e <file>`, `--experiment <file>` (**mandatory**) Specify file containing experiment specification ([see experiment file format](https://github.com/sybila/parasim/wiki/Experiment-File-Format)).
* `-r`, `--result` Do not execute experiment, only print result contained in file specified by experiment file (see above). **Note:** This will result in error when the result file is not present.
* `-csv <file>`, `--csv <file>` Specify destination file for CSV export.
* `-b`, `--batch` Disable result plotting.
* `-s`, `--server` [Deprecated] Start Parasim server only.
* `-t`, `--terminal` Use command line interface.

### Examples
```bash
parasim.sh -e experiment.properties
```
Execute experiment described in `experiment.properties` ([see experiment file format](https://github.com/sybila/parasim/wiki/Experiment-File-Format)) and plot result.

```bash
parasim.sh -e experiment.properties -r
```
Load result from file referenced in `experiment.properties` and plot it. Do not execute the experiment.
**Note:** The result file must exist, i.e. the experiment has already had to be executed.

## Building Parasim

To build and test Parasim, run:

    ./gradlew clean build

You can also use `assemble` to build without testing or `check` to explicitly run tests.

If you want to run Parasim directly, withouth creating distribution binaries, use:

    ./gradlew run

If you want to create the distribution archive, run:

    ./gradlew distZip

The distribution .zip will then be located in the application/build/distributions folder. Alternatively, you can generated the contents of the .zip file into application/build/install by calling:

    ./gradlew installDist
    
Finally, to regenerate license headers use gradle commands `license` and `licenseFormat`
(If you have your own Gradle insallation, you can run gradle instead of ./gradlew)
