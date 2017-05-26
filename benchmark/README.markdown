# Parasim Benchmark

WARNING: Distributed computation is now deprecated.

In order to run benchmarks, parasim must be built using the `installDist` command.

This folder contains tool to benchmark Parasim. Configuration files are placed
in the `config` directory and are written for execution on `anna.fi.muni.cz`.

The format of a configuration file name is `parasim-<environment>-<number>.xml`.

If the file name of the configuration file contains a "dist" string, the benchmark
tool assumes that servers has to be started. `../servers-start.sh` script is used
for this purpose.

Any experiment project located in `experiments` directory has to contain an
experiment file `benchmark.experiment.properties`.

## Benchmark List File

The benchmark tool is configured via a file presenting a list of pairs of `configuration:experiment`.
For example:

	parasim-shared-1:lotkav-long-property

Spaces and any other colons are not allowed.

## Execution

	./bench.sh <benchmarki list file>

