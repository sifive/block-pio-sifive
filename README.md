# Overview

This branch represents the Parallel IO (PIO) block and loopback Verification IP (VIP) in their completed state. 
This README describes the commands for running tests, creating an FPGA bitstream, and generating documentation.

The earlier [`preonboarded`](https://github.com/sifive/block-pio-sifive/blob/preonboarded/README.md) branch has step-by-step 
instructions detailing how the PIO block was created and integrated into the test socket framework.

The PIO block communicates to the CPU through an AXI4 interface. 
This interface is used to read from and drive the odata, oenable, and idata control registers that map to top-level ports. To integrate this block we need make the odata, oenable, and idata ports available at the top-level of design and connect the AXI4 interface to the Test Socket's periphery bus.

There is also an accompanying VIP for testing our block. 
The loopback VIP needs to be instantiated in the test harness and be connected to the odata, oenable, and idata pads. 
The loopback block outputs the xor of oenable and odata to idata.


## Getting Started

### Dependencies

#### wit

* Wit is a workspace manager
* Use version 0.11.0
* Please see instructions on the [wit README](https://github.com/sifive/wit)

#### wake

* wake is a build tool
* Use version 0.17.1
* For installation instructions see the [wake README](https://github.com/sifive/wake/tree/v0.17.1#installing-dependencies)
* [wake tutorial](https://github.com/sifive/wake/blob/v0.17.1/share/doc/wake/tutorial.md)
* [wake quickref](https://github.com/sifive/wake/blob/v0.17.1/share/doc/wake/quickref.md)

#### duh

* `npm i duh@1.13.1`
* duh assists in IP onboarding
* Please see instructions on [duh README](https://github.com/sifive/duh)

#### Other dependencies

* riscv-gnu-toolchain
  * `freedom-metal` requires a toolchain from here https://www.sifive.com/boards (scroll down)
* device-tree-compiler
  * Available via most package managers:
  * eg. Ubuntu `sudo apt-get install device-tree-compiler`
* libfdt-dev
  * Available via most package managers:
  * eg. Ubuntu `sudo apt-get install libfdt-dev`
* Verilog simulator
  * VCS
  * Xcelium
  * Verilator
    *  v3.922
    * See [verilator install instructions](https://www.veripool.org/projects/verilator/wiki/Installing)
    * Verilator additionally requires Perl v5.22.2
* python3 and pip3
* ruby v2.3.7 or later

#### Environment package
In order for wake to be able to provide the correct environments for jobs
that need to run the above tools, you will need an environment package that
provides wake job runners that can fulfill those environments for your
particular system. An example is
[environment-example-sifive](https://github.com/sifive/environment-example-sifive).

### Initialize the workspace
```
# Create a workspace, this will also fetch all dependencies
wit init workspace -a git@github.com:sifive/block-pio-sifive.git
cd workspace/

# Add an environment package to provide runners for the required tools
wit add-pkg $MY_ENVIRONMENT_PACKAGE

# Resolve and fetch all transitive dependencies
wit update

# Initialize the workspace for building with wake
wake --init .
```

## Running tests

We can run PIO simulation tests with the following command.
```
wake 'runSim ${dut plan}'
```

The `run` function uses the default output directory and simulator.
`${workspace root}/build` is the default output directory, and VCS is the default
simulator.

The available simulators are `VCS`, `VCS_Waves`, `Verilator`, `Verilator_Waves`,
`Xcelium`, and `Xcelium_Waves`. To run tests with a different simulator use
```
wake 'runSimWith ${dut plan} ${VCS|VCS_Waves|Verilator|Verilator_Waves|Xcelium|Xcelium_Waves}'
```

Running tests will invoke the RocketChip generator and Firrtl to
generate verilog files for the selected DUT plan, gcc to compile the selected
programs, and the selected simulator to run each program on its respective DUT.

Each test will create a simulation output directory with the name
`${build_dir}/${dut_name}/sim/${simulator}/results/${program_name}`. This directory
contains `sim.err` and `sim.out` files containing the contents of stderr and
stdout respectively. There is also a `console.log` file which is where `printf`
prints to by default. Waveform files and any other simulation files can also
be found in this directory.

Verilog files can be found in `${build_dir}/${dut_name}/verilog`.

Firrtl files can be found in `${build_dir}/${dut_name}/firrtl`.

bin, elf, and hex files of the test program can be found in
`${build_dir}/${dut_name}/program/${program_name}`.

## Creating a Bitstream

We can also map our test-socket and pio block to a VC707 fpga using `makeVC707TestSocketDUT`.
To create the bitstream, run
```
wake 'runBitstream "vc707" pioVC707DUT'
```
The bitstream will be placed at `build/api-generator-sifive/pioVC707DUT/mcs/obj/VC707Shell.bit`


## Creating Documentation
The earlier onboarding process created text describing the PIO block and how it is used.
The following command generates a test "Onboarding" document which includes that text.
```
wake makeOnboardingDocument pioDUT
```
The command creates two files, `pioDUT.html` and `pioDut.adoc`, both in the directory `build/api-generator-sifive/pioDUT/documentation`. 
The .html file can be viewed directly in a web browser, and the .adoc file contains AsciiDoc which can be used for further processing,
See the [AsciiDoctor PDF project](https://asciidoctor.org/docs/asciidoctor-pdf) for information on converting AsciiDoc to PDF.


## Wrap up
Checkout branch [`preonboarded`](https://github.com/sifive/block-pio-sifive/blob/preonboarded/README.md) 
if you would like to go back to the beginning and see the steps used for onboarding this PIO block.
