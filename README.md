## Getting Started

### Dependencies

#### wit

* Wit is a workspace manager
* requires version 0.6
* Please see instructions on the [wit README](https://github.com/sifive/wit)

#### wake

* wake is a build tool
* Use v0.15
* For installation instructions see the [wake README](https://github.com/sifive/wake/tree/v0.15.0#installing-dependencies)
* [wake tutorial](https://github.com/sifive/wake/blob/v0.15.0/share/doc/wake/tutorial.md)
* [wake quickref](https://github.com/sifive/wake/blob/v0.15.0/share/doc/wake/quickref.md)

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

## Overview
This demo block consists of a pio IP and a loopback VIP, their `duh`
json5 descriptions, and a couple c tests.

The pio block communicates to the CPU through an AXI4 interface. This interface
is used to read from and drive the `odata`, `oenable`, and `idata` ports. To
integrate this block we need make the `odata`, `oenable`, and `idata` ports
available at the top-level of design and connect the AXI4 interface to Rocket's
periphery bus.

There is also an accompanying vip for testing our block. The loopback vip needs
to be instantiated in the testharness and be connected to the `odata`,
`oenable`, and `idata` pads. The loopback block outputs the xor of `oenable`
and `odata` to `idata`.

## Requirements for testing a block
  1. a scala project containing chisel source code for the block
  2. a wake `Program` described in Making a test program
  3. a wake `DUT` described in Making a skeleton DUT
  4. a wake `Test` described in Making and publishing a test

## Making the demo block
### 1. Creating the `duh` description
  First, install version 1.10.0 of `duh`
  ```
  npm i duh@1.13.1
  ```
  `duh` descriptions for the pio and loopback blocks are already included with
  this repo. See https://github.com/sifive/duh for instructions on how to create
  these files.

### 2. Generating scala code with `duh`

  `block-pio-sifive/pio.json5` and `block-pio-sifive/loopback.json5`
  contain descriptions of the ports, parameters, buses, and CSRs of the pio
  and loopback blocks respectively. This description is fed into `duh` to
  generate boilerplate scala code for integrating the block into Rocket.
  This assumes node installed and the
  current directory is the workspace root.
  ```
  duh-export-scala block-pio-sifive/loopback.json5 -o block-pio-sifive/craft/loopback/src
  ```
  Then do the same for the pio block also from the workspace root.
  ```
  duh-export-scala block-pio-sifive/pio.json5 -o block-pio-sifive/craft/pio/src
  ```
  You should now see `${moduleName}.scala` and
  `${moduleName}-base.scala` files in the `block-pio-sifive/craft/pio/src` and
  `block-pio-sifive/craft/loopback/src` directories.

### 3. Extending `duh` generated base classes

  `${moduleName}-base.scala` files should not be modified; these files
  contain generated base scala wrappers for our blocks. These base classes
  instantiate our verilog blocks and connect the buses that are described
  in the json5 file. They can be extended to add extra functionality. Stubs
  that extend the base classes are generated in the `${moduleName}.scala`
  files. These files can be edited and will not be overwritten by
  `duh-export-scala.js` if they already exist.

  Since our loopback block is simple, we can use the `duh` generated scala
  as is. The pio block, however, will need to be extended. `duh` only
  generates connections for the AXI4 interface of our block, so `clk`,
  `reset_n`, `odata`, `oenable`, and `idata` are still dangling.

  Open up `block-pio-sifive/craft/pio/src/pio.scala`. There will be stubs
  along with comments indicating where you should add your own code.

  The top-level of the block will be called `N${moduleName}Top`. The hierarchy
  of the generated scala module will look like
  ```
  top: NpioTop
  └── imp: Lpio
      └── blackbox: pio
  ```
  The `imp` instance of the top-level block instantiates our verilog module
  using a Chisel BlackBox. It instantiates interface nodes that correspond to
  the buses described in the `duh` document and connects them to the ports of the
  blackbox according to the port-map from the `duh` document. The
  `N${moduleName}Top` module that wraps the `imp` instance contains any
  necessary adapters such as TileLink-to-AXI4 adapters.
  The `NpioTop` class has a corresponding companion object that contains an
  `attach` method that describes how the block should be connected to the
  Rocket subsystem.

  First, add an import for the loopback block to the list of imports at the top
  of `block-pio-sifive/craft/pio/src/pio.scala`.
  ```scala
  import sifive.vip.loopback._
  ```

  We need to define the ports that we want our block to have. Put the
  following lines after the imports in the same file.
  ```scala
  class NpioTopIO(
    val pioWidth: Int
  ) extends Bundle {
    val odata = Output(UInt(pioWidth.W))
    val oenable = Output(UInt(pioWidth.W))
    val idata = Input(UInt(pioWidth.W))
  }
  ```

  Then edit the `NpioTop` class to add connections to the dangling
  ports. Copy the following lines into the body of the `NpioTop`class in
  `block-pio-sifive/craft/pio/src/pio.scala`.
  ```scala
    // route the ports of the black box to this sink
    val ioBridgeSink = BundleBridgeSink[pioBlackBoxIO]()
    ioBridgeSink := imp.ioBridgeSource

    // create a new ports for odata, oenable, and idata
    val ioBridgeSource = BundleBridgeSource(() => new NpioTopIO(c.blackbox.pioWidth))

    // logic to connect ioBridgeSink/Source nodes
    override lazy val module = new LazyModuleImp(this) {

      // connect the clock and negedge reset to the default clock and reset
      ioBridgeSink.bundle.clk     := clock.asUInt
      ioBridgeSink.bundle.reset_n := !(reset.toBool)

      // connect ioBridge source and sink
      ioBridgeSource.bundle.odata   := ioBridgeSink.bundle.odata
      ioBridgeSource.bundle.oenable := ioBridgeSink.bundle.oenable
      ioBridgeSink.bundle.idata     := ioBridgeSource.bundle.idata
    }
  ```

  Finally, edit the `attach` method in the `NpioTop` companion object.
  ```scala
      implicit val p: Parameters = bap.p

      // instantiate and connect the loopback vip in the testharness
      bap.testHarness {
        // instantiate the loopback vip
        val loopbackP = NloopbackTopParams(
          blackbox = loopbackParams(
            pioWidth = c.blackbox.pioWidth,
            cacheBlockBytes = p(CacheBlockBytes)))
        val loopback = NloopbackTop.attach(loopbackP)(bap)

        // route loopback signals to the testharness
        val loopbackNode = BundleBridgeSink[loopbackBlackBoxIO]()
        loopbackNode := loopback.imp.ioBridgeSource

        // route pio signals to the testharness
        val pioNode = BundleBridgeSink[NpioTopIO]()
        pioNode := pio.ioBridgeSource

        // connect the pio and loopback signals
        InModuleBody {
          loopbackNode.bundle.odata   := pioNode.bundle.odata
          loopbackNode.bundle.oenable := pioNode.bundle.oenable
          pioNode.bundle.idata        := loopbackNode.bundle.idata
        }
      }
  ```

  Now we need to set the base address of our pio block. The demo tests expects
  the block to be at base address 0x6000, so edit the `WithpioTop` config class
  so that it looks like this:
  ```
  class WithpioTop extends Config(
    new WithpioTopBase(
      ctrl_base = 0x60000L
    )
  ```

  In order to make our new scala files visible to wake we need to add them to
  git. In the `block-pio-sifive` directory run the following to add the scala files.
  ```
  git add craft
  ```

### 4. Creating a wake `ScalaModule`

  Now that we have our scala source code, we need to define `ScalaModule`s to
  describe our scala projects in wake. A `ScalaModule` is defined as follows:
  ```
  tuple ScalaModule =
    global Name:               String
    global RootDir:            String
    global ScalaVersion:       ScalaVersion
    global IvyDeps:            List IvyDep
    global Deps:               List ScalaModule
    global SourceDirs:         List String
    global ResourceDirs:       List String
    global FnGeneratedSources: Unit => List Path
    global ScalacOptions:      List String
  ```

  `SourceDirs` and `ResourceDirs` should be relative to the `RootDir`.
  `makeScalaModule` can be used to contruct a `ScalaModule`, or you can use
  `makeScalaModuleFromJSON` if you already have an `ivydenendencies.json` file.

  Since our generated scala modules are pretty simple the only optional fields
  we need to set are `ScalacOptions`, `Deps`,  and `SourceDirs`. All other
  fields will default to `Nil`.

  The loopback block uses components from
  [soc-testsocket-sifive](https://github.com/sifive/soc-testsocket-sifive) and
  [sifive-blocks](https://github.com/sifive/sifive-blocks) so we need to add the
  already defined `sifiveBlocksScalaModule` and `sifiveSkeletonScalaModule` as
  dependencies. Add this definition to `block-pio-sifive/wake/demo.wake` to define the
  `loopback` `ScalaModule`.
  ```
  def loopbackScalaModule =
    def name = "loopback"
    def path = "{here}/../craft/loopback"
    def scalaVersion = sifiveSkeletonScalaModule.getScalaModuleScalaVersion
    def deps = sifiveBlocksScalaModule, sifiveSkeletonScalaModule, Nil
    makeScalaModule name path scalaVersion
    | setScalaModuleSourceDirs ("src", Nil)
    | setScalaModuleDeps deps
    | setScalaModuleScalacOptions ("-Xsource:2.11", Nil)
  ```

  The `pio` module is almost exactly the same. The only changes are the name,
  root directory, and dependencies. We imported components from `loopback`
  into the `pio` project so we need to add `loopbackScalaModule` as a
  dependency here. Copy the following into `block-pio-sifive/wake/demo.wake`
  ```
  def pioModule =
    def name = "pio"
    def rootDir = "{here}/../craft/pio"
    def scalaVersion = sifiveSkeletonScalaModule.getScalaModuleScalaVersion
    def deps = loopbackScalaModule, sifiveBlocksScalaModule, sifiveSkeletonScalaModule, Nil
    makeScalaModule name rootDir scalaVersion
    | setScalaModuleSourceDirs ("src", Nil)
    | setScalaModuleDeps deps
    | setScalaModuleScalacOptions ("-Xsource:2.11", Nil)
  ```
### 6. Creating a wake `Block`

  Now that we have a scala project we can create a `ScalaBlock`. A `ScalaBlock`
  is just a `ScalaModule` associated with a config. Use `makeScalaBlock` to
  create a `ScalaBlock`. It has the following type signature.
  ```
  def myBlock
    def project = ${scala module that contains config} # ScalaModule
    def config = ${name of the config to be added to DUT} # String
    makeScalaBlock name source
  ```

  `duh` generates a config called `sifive.blocks.{moduleName}.With{moduleName}Top`.
  Including this config in our DUT will instantiate the block in the subsystem using
  the `attach` function defined by `duh`. Copy the following lines for creating the
  `pio` block into `block-pio-sifive/wake/demo.wake`.
  ```
  def pioBlock =
    def scalaModule = pioModule
    def config = "sifive.blocks.pio.WithpioTop"
    makeScalaBlock scalaModule config
  ```

### 7. Adding simulation options hooks
  There are two main publish/subscribe targets for adding simulation option hooks:
  `dutSimCompileOptionsHooks` and `dutSimExecuteOptionsHooks` for compile and
  runtime options respectively. These hooks are functions of the type
  `DUT => Option (a => a)`. A `None` return value means that no modifications
  are made. Otherwise the returned transformation on `a` is applied.

  `dutSimCompileOptionsHooks` operate on `DUTSimCompileOptions`.
  `DUTSimCompileOptions` is defined as follows:
  ```
  tuple DUTSimCompileOptions =
    global IncludeDirs:    List String
    global Defines:        List NamedArg
    global SourceFiles:    List Path
    global Plusargs:       List NamedArg
  ```

  An example hook might look like:
  ```
  publish dutSimCompileOptionsHooks =
    def hook dut =
      if dut.getDUTName ==~ "myDUT"
      then
        def optionsTransform options =
          options
          | editDUTSimCompileOptionsIncludeDirs ("myIncludes", _)
          | editDUTSimCompileOptionsDefines (
            NamedArg        "MY_DEFINE",
            NamedArgInteger "MY_DEFINE_INTEGER" 1,
            NamedArgDouble  "MY_DEFINE_DOUBLE"  1.0,
            NamedArgString  "MY_DEFINE_STRING"  "string",
            NamedArgPath    "MY_DEFINE_PATH"    "mySourceFile".source,
            _
          )
          | editDUTSimCompileOptionsPlusargs (NamedArgString "my_plusarg" "value", _)
          | editDUTSimCompileOptionsSourceFiles ("mySourceFile".source, _)
        Some optionsTransform
      else None
    hook, Nil
  ```

  The `pio` block needs `pio.v` and `loopback.v` to be added to the list
  of files to be compiled in the simulation so we need to add hooks to
  `dutSimCompileOptionsHooks` to add those files. There is a helper function
  `makeBlackBoxHook` that takes a name and a transform function and returns
  a hook that will apply the transform is the blackbox of the same name is found
  in the `DUT`. We can use this to tell the simulator about our pio/loopback
  source files because `duh` will generate blackboxes of those modules for us.
  Copy the following hook defninitions into `block-pio-sifive/wake/demo.wake`.
  ```
  publish dutSimCompileOptionsHooks = pioHook, loopbackHook, Nil

  def loopbackHook =
    def name = "loopback"
    def addSources = source "{here}/../rtl/loopback.v", _
    makeBlackBoxHook name (editDUTSimCompileOptionsSourceFiles addSources)

  def pioHook =
    def name = "pio"
    def addSources = source "{here}/../rtl/pio.v", _
    makeBlackBoxHook name (editDUTSimCompileOptionsSourceFiles addSources)
  ```

## Making a test

Currently, only c integration tests are supported. A simple test is included
with this repository in `block-pio-sifive/tests/demo/main.c`. It looks like this.
```c
int main()
{
  // read/write to axi block
  volatile uint32_t * pio = (uint32_t *) 0x60000;

  volatile uint32_t * odata   = pio;
  volatile uint32_t * oenable = pio + 1;
  volatile uint32_t * idata   = pio + 2;

  int fail = 0;
  int test_len = 100;
  for (int i = 0; i < test_len; i++) {
    *odata = i;
    *oenable = test_len - i;

    fail |= ((*odata ^ *oenable) != *idata);
  }

  return fail;
}
```

To tell wake how to compile this test we need to create a `TestProgramPlan`.
`TestProgramPlan` is defined as follows:
```
tuple TestProgramPlan =
  global Name:        String
  global CFlags:      List String
  global ASFlags:     List String
  global CFiles:      List Path
  global IncludeDirs: List String
  global Sources:     List Path
  global Filter:      DUTProgramCompiler => Boolean
```
A `TestProgramPlan` should only contain machine agnostic flags, since the
same program plan may be run on multiple different configs. Machine-specific
options should be filled in by the `DUTProgramCompiler`. You can specify what
`DUTProgramCompiler`s are valid for this plan using the `Filter` field.

To create a `TestProgramPlan` use `makeTestProgramPlan`. It has the following
type signature.
```
def mySimpleProgram =
  def programName = ${name of program} # String
  def cfiles = ${files to compile} # List SPath
  makeTestProgramPlan programName cfiles # Program
```

Currently, the only supported `DUTProgramCompiler` is
`freedomMetalDUTProgramCompiler` which uses
[freedom-metal](https://github.com/sifive/freedom-metal).
`freedomMetalDUTProgramCompiler` provides standard libraries and will compile
and link programs according the DTS.

Since the demo test is pretty simple, we only need to specify the cfiles and the
program name, and we can use the default parameters. Copy the following into
`block-pio-sifive/wake/demo.wake` to create a `TestProgramPlan` for `block-pio-sifive/tests/demo/main.c`.
```
def demo =
  def programName = "demo"
  def cFiles = source "{here}/../tests/demo/main.c", Nil
  makeTestProgramPlan programName cFiles
```

Suppose we did not want to hardcode the address of the pio block and we edited
our test to look like this.
```c
int main()
{
  // read/write to axi block
  volatile uint32_t * pio = (uint32_t *) PIO;

  volatile uint32_t * odata   = pio;
  volatile uint32_t * oenable = pio + 1;
  volatile uint32_t * idata   = pio + 2;

  int fail = 0;
  int test_len = 100;
  for (int i = 0; i < test_len; i++) {
    *odata = i;
    *oenable = test_len - i;

    fail |= ((*odata ^ *oenable) != *idata);
  }

  return fail;
}
```

We would need to add an argument to the c compiler. Our new wake program would
then look like this.
```
def demo =
  def programName = "demo"
  def cFiles = source "{here}/../tests/demo/main.c", Nil
  makeTestProgramPlan programName cFiles
  | editTestProgramPlanCFlags ("-DPIO=0x60000", _)
```

A more complicated program like dhrystone looks like this. Dhrystone uses. The
files for dhrystone are included in this repo.
```
def dhrystone =
  def programName = "dhrystone"
  def prefix = "{here}/../tests/dhrystone"
  def cFiles = source "{prefix}/dhry_1.c", source "{prefix}/dhry_2.c", Nil
  def withIncludeDirs = prefix, _
  def withExtraCFlags =
    def iterations = 300
    "-specs=nano.specs", "-O3", "-DTIME", "-DNOENUM", "-Wno-implicit",
    "-mexplicit-relocs", "-save-temps", "-fno-inline", "-fno-builtin-printf",
    "-fno-common", "-falign-functions=4", "-Xlinker", "--defsym=__stack_size=0x800",
    "-DDHRY_ITERS={str iterations}", _
  makeTestProgramPlan programName cFiles
  | editTestProgramPlanCFlags withExtraCFlags
  | editTestProgramPlanIncludeDirs withIncludeDirs
```

## Making a skeleton DUT
To run our program we need a `DUT`. A `DUT` is a collection of source files,
metadata and an object model that describes the design.
[rocket-chip-wake](https://github.com/sifive/rocket-chip-wake) includes a `DUT`
constructor called `rocketChipDUTMaker` that takes a `RocketChipDUTPlan` and
returns a `DUT`. To construct a `DUT` to test our block we can use the helper
function `makeTestSocketDUT {name} {blocks}`.

`makeTestSocketDUT` constructs a `RocketChipDUTPlan` that includes a simulation
uart, a test-finisher, and the extra blocks supplied by the `blocks` argument.

Copy the following lines for creating a test `DUT` for the pio block into
`block-pio-sifive/wake/demo.wake`.
```
global def pioDUT =
  def name = "pioDUT"
  def blocks = pioBlock, Nil
  makeTestSocketDUT name blocks
```

## Making and publishing a test
A wake `DUTTest` combines all the necessary components for compiling a design and
simulating a test program running on that design. Use `makeDUTTest` to create a
`DUTTest`. It has the following type signature.
```
publish test =
  def name = ${name of test} # String
  def filter = ${returns True only for DUTs where this test is applicable} # DUT => Boolean
  def program = ${test program to run} # Program
  def bootloader = ${bootloader to load the test program} # SimBootloader
  def plusargs = ${extra plusargs to use for simulation} # List NamedArg
  makeDUTTest name filter program bootloader plusargs
```

The `makeBlockTest` is a helper function that provides a default `filter` and
`bootloader` for tests associated with a `ScalaBlock`. It has the following type
signature.
```
  def myBlockTest =
  def name = ${name of the test} # String
  def block = ${the block this test is associated with} # ScalaBlock
  def program = ${test program to run} # Program
  def plusargs = ${extra plusargs to use for simulation} # List NamedArg
  makeBlockTest name block program plusargs =
```

Copy the following lines into `block-pio-sifive/wake/demo.wake` to create and publish the demo
test for the pio block. Publishing to `dutTests` will register this test so that it
is automatically run whenever an applicable `DUT` is being tested (see next
section).
```
publish dutTests = demoPioTest, Nil

global def demoPioTest =
  def name = "demo"
  def block = pioBlock
  def program = demo
  def plusargs =
    NamedArg        "verbose",
    NamedArgInteger "random_seed"      1234,
    NamedArgInteger "tilelink_timeout" 16000,
    NamedArgInteger "max-cycles"       50000,
    Nil
  makeBlockTest name block program plusargs
```

The `verbose` plusarg toggles printing of the instruction trace to stderr. The
stdout, stderr, output of `printf`, and waveform files will be output to the
simulation result directory (details in the next section).

## Running tests

Now that we have a test to run we can run it with the following command.
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

## Wrap up
Checkout tag `complete` if you would like to see how the repo should look
like after completing the tutorial.
