# Parasim

## Requirements

* java (at least version 7)
* maven
* octave (simulation framework is based on octave)
* git (optional)

## Packaging, Installation, Deployment

From the root directory:

    ./build-all.sh clean [deploy|install|package]

or:

    mvn clean [deploy|install|package] -P[application|core|extensions|license|model|parent|util]

## Executing

On unix-like operation systes parasim may be executed from command line using the [`parasim.sh`](https://github.com/sybila/parasim/blob/master/parasim-cli.sh) script after building it.

### Command Line Options
* `-v`, `--version` Print parasim version and exits.
* `-h`, `--help` Print command line (condensed) options and exits.

* `-c <file>`, `--confing <file>` Specify parasim configuration file (other than default).
* `-e <file>`, `--experiment <file>` (**mandatory**) Specify file containing experiment specification ([see experiment file format](https://github.com/sybila/parasim/wiki/Experiment-File-Format)).
* `-r`, `--result` Do not execute experiment, only print result contained in file specified by experiment file (see above). **Note:** This will result in error when the result file is not present.
* `-csv <file>`, `--csv <file>` Specify destination file for CSV export.
* `-b`, `--batch` Disable result plotting.
* `-s`, `--server` Start Parasim server only.
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

## Generate License Headers

Go to the module where you want to update license headers:

    ./build-all.sh clean validate -Pgenerate-license
