# Overview
This branch represents the Parallel IO (PIO) block and loopback Verification
IP (VIP) in their preonboarded state. This README is a step-by-step tutorial
of how to onboard the PIO block.

The PIO block communicates to the CPU through an AXI4 interface. This interface
is used to read from and drive the `odata`, `oenable`, and `idata` control
registers that map to top-level ports. To integrate this block we need make the
odata, oenable, and idata ports available at the top-level of design and
connect the AXI4 interface to the Test Socket's periphery bus.

There is also an accompanying VIP for testing our block. The loopback VIP needs
to be instantiated in the test harness and be connected to the odata, oenable,
and idata pads. The loopback block outputs the xor of `oenable` and `odata` to
`idata`.

Sections in this README:
* [Checking out a workspace](#checking-out-a-workspace)
* [Creating the DUH document](#creating-the-duh-document)
* [Scala integration](#scala-integration)
* [Wake integration](#wake-integration)
* [Parameterizing your block](#parameterizing-your-block)
* [Documenting your block](#documenting-your-block)

## Checking out a workspace
This repo uses [wit](git@github.com:sifive/wit.git) for workspace management.
This section describes how to use wit to checkout a workspace with all the
correct versions of packages that this repo depends on. To learn more about
how to use wit see this [tutorial](https://github.com/sifive/wit/blob/0.11.x/share/doc/wit/tutorial.md).

#### Environment package
In order for wake to be able to provide the correct environments for jobs
that need to run the above tools, you will need an environment package that
provides wake job runners that can fulfill those environments for your
particular system. An example is
[environment-example-sifive](https://github.com/sifive/environment-example-sifive).

### Initialize the workspace
```
# Create a workspace, this will also fetch all dependencies
wit init workspace -a git@github.com:sifive/block-pio-sifive.git::preonboarded
cd workspace/

# Add an environment package to provide runners for the required tools
wit add-pkg $MY_ENVIRONMENT_PACKAGE

# Resolve and fetch all transitive dependencies
wit update

# Initialize the workspace for building with wake
wake --init .
```


## Creating the DUH document
NOTE: this tutorial was made using version 1.15.0 of DUH

The DUH document is a JSON5 file that is primarily used to describe an IP block
similar to IPXACT. The DUH JSON schema is defined
[here](https://github.com/sifive/duh-schema). DUH is also a suite of tools that
help users author DUH documents and generate useful artifacts from them. This
section describes how to use some of the DUH tools to author a DUH document for
the PIO block.

For more detailed documentation on DUH see the
[README](https://github.com/sifive/duh/blob/master/README.md)

Subsections:
* [Installation](#installing-duh)
  - how to install DUH
* [Initialization](#initializing-the-duh-document)
  - how to initialize a DUH document
* [Ports](#importing-verilog-ports)
  - how to automatically import port definitions into DUH from verilog source
* [Bus Interfaces](#define-bus-interfaces)
  - how to automatically infer bus definitions
* [Memory Maps](#define-memory-maps)
  - how to define memory maps in DUH
* [Parameter Schema](#define-parameter-schema)
  - how to define a parameter schema in DUH
* [Loopback](#creating-the-loopback-duh-document)
  - how to create a DUH document for the loopback VIP
* [Validation](#validation)
  - how to validate that our document conforms to the DUH schema

### Installing DUH
First you will need to install [DUH](https://github.com/sifive/duh). To install
DUH in your current directory run
```bash
npm i duh@1.16.1
```

This will create a `node_modules` subdirectory in your current directory with
all the package sources. The `duh` executable along with other useful tools are
installed in `node_modules/.bin`. You may want to add this to your path with
`export PATH=./node_modules/.bin:$PATH` for convenience.

### Initializing the DUH document
To create an initial DUH document run `duh init` and answer the prompts. For
this tutorial we will name the block `pio` and write the DUH document to
`pio.json5`
<pre>
duh init
? <b>Document file name</b> pio.json5
? <b>Block name</b> pio
? <b>version</b> 0.1.0
? <b>Please write a short description about the block</b> A Parallel IO block
? <b>Block type</b> component
? <b>Source type</b> Verilog
</pre>

Now we can open the generated `pio.json5` in our editor of choice.

Since we are onboarding a Verilog IP block, in `pio.json5` we should set the `fileSets` field in
our DUH component to
```javascript
fileSets: {
    VerilogFiles: ['pio.sv']
}
```

### Importing verilog ports
Fill in the port definitions of the block in the DUH document. The
`duh-import-verilog-ports` tool can parse the verilog and fill in the
definitions for you.
```bash
cat rtl/verilog/pio/pio.sv | duh-import-verilog-ports pio.json5
```

### Define bus interfaces
See this [README](https://github.com/sifive/block-ark/blob/master/README.md#walkthrough-example-import-using-duh)
for a more detailed walkthrough of `duh-portinf`

Now that we have port definitions, we need to define port mappings for any bus
interfaces that this block implements. The PIO block has an AXI4-Lite
interface for controlling the `ODATA`, `OENABLE`, and `IDATA` registers. You can
either define bus interfaces manually or use `duh-portinf` to infer candidate
bus interfaces. `duh-portinf` requires us to supply specifications of the buses
we want to infer port mappings for. We can use the default `duh-bus` included
with our DUH install, since it contains a specification for AXI4-Lite.
```bash
duh-portinf pio.json5 --duh-bus $duh_install_dir/node_modules/duh-bus -o pio.json5
```

You should now see the following fields in the DUH component of pio.json5
```javascript
...
"busInterfaces": [
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_0-prefix_t_ctrl-slave-AXI4-Lite_rtl"}
],
"busInterfaceAlts": [
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_1-prefix_t_ctrl-slave-AXI4_rtl"},
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_2-prefix_t_ctrl-master-AHBLite_rtl"},
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_3-prefix_t_ctrl-master-AXI4-Lite_rtl"},
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_4-prefix_t_ctrl-slave-AXI4Stream_rtl"},
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_5-prefix_t_ctrl-slave-DPRAM_rtl"},
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_6-prefix_t_ctrl-master-AXI4_rtl"},
    {"$ref": "#/definitions/busDefinitions/busint-portgroup_0-mapping_7-prefix_t_ctrl-slave-SPRAM_rtl"}
],
...
```

The first candidate inferred by `duh-portinf` is pretty close but is missing
the `ACLK` and `ARESETn` signals. Find the node corresponding to the reference
in `busInterfaces` and add the following fields to its port map
```javascript
"ACLK": "clk",
"ARESETn": "reset_n",
```

The final result should look like
```javascript
"busint-portgroup_0-mapping_0-prefix_t_ctrl-slave-AXI4-Lite_rtl": {
    "name": "t_ctrl",
    "interfaceMode": "slave",
    "busType": {
        "vendor": "amba.com",
        "library": "AMBA4",
        "name": "AXI4-Lite",
        "version": "r0p0_0"
    },
    "abstractionTypes": [
        {
            "viewRef": "RTLview",
            "portMaps": {
                "ACLK": "clk",
                "ARESETn": "reset_n",

                "BVALID": "t_ctrl_bvalid",
                "WVALID": "t_ctrl_wvalid",
                "AWVALID": "t_ctrl_awvalid",
                "WSTRB": "t_ctrl_wstrb",
                "RDATA": "t_ctrl_rdata",
                "ARREADY": "t_ctrl_arready",
                "ARPROT": "t_ctrl_arprot",
                "AWREADY": "t_ctrl_awready",
                "RRESP": "t_ctrl_rresp",
                "AWADDR": "t_ctrl_awaddr",
                "BRESP": "t_ctrl_bresp",
                "AWPROT": "t_ctrl_awprot",
                "WREADY": "t_ctrl_wready",
                "WDATA": "t_ctrl_wdata",
                "ARVALID": "t_ctrl_arvalid",
                "RVALID": "t_ctrl_rvalid",
                "BREADY": "t_ctrl_bready",
                "ARADDR": "t_ctrl_araddr",
                "RREADY": "t_ctrl_rready"
            }
        }
    ]
}
```

### Define memory maps
Next we need to define the memory maps of this block. This block contains three
control registers: `ODATA`, `OENABLE`, and `IDATA`. Add a `memoryMaps` field to
the `component` object in pio.json5 as follows.
```javascript
"memoryMaps": [{
    name: 'CSR',
    addressBlocks: [{
        name: 'csrAddressBlock',
        baseAddress: 0,
        range: 1024, width: 32,
        usage: 'register',
        volatile: false, access: 'read-write',
        registers: [{
            name: 'ODATA',
            addressOffset: 0, size: 32,
            displayName: 'Output Data Register',
            fields: [{name: 'data', bitWidth: 32, bitOffset: 0}]
        }, {
            name: 'OENABLE', addressOffset: 4, size: 32,
            displayName: 'Data direction',
            description: 'determines whether the pin is an input or an output. If the data direction bit is a 1, then the pin is an input',
            fields: [{name: 'data', bitWidth: 32, bitOffset: 0}]
        }, {
            name: 'IDATA', addressOffset: 8, size: 32,
            displayName: 'Input data',
            description: 'read the port pins',
            fields: [{name: 'data', bitWidth: 32, bitOffset: 0}]
        }]
    }]
}]
```

### Define parameter schema
Finally, we need to describe the parameters of the PIO block. This block has
`addrWidth`, `dataWidth`, and `pioWidth` parameters that need to be described
in the DUH document. The `pSchema` field of `component` is a JSON schema that
describes the parameters of the block as a JSON object. Add a `pSchema` field
to the `component` object in pio.json5 as follows.
```javascript
pSchema: {
    type: 'object',
    properties: {
        addrWidth: {
            title: 'Address bus width',
            type: 'integer', minimum: 6, maximum: 32, default: 12
        },
        dataWidth: {
            title: 'Data bus width',
            type: 'integer', minimum: 32, maximum: 64, default: 32
        },
        pioWidth: {
            title: 'Number of IO pads',
            type: 'integer', minimum: 1, maximum: 32, default: 32
        },
        writeStrobeWidth: {
            title: 'Write strobe width',
            type: 'integer', minimum: 1, maximum: 4, default: 4
        }
    }
}
```

### Creating the loopback DUH document
To test our PIO block we will use a simple loopback VIP. This VIP block can be
onboarded with a DUH document similarly to the PIO block. Follow the same steps
to onboard the loopback VIP as we did for the PIO block. Since the loopback
does not have any bus interfaces we can skip the `duh-portinf` step.

initialization
<pre>
duh init
? <b>Document file name</b> loopback.json5
? <b>Block name</b> loopback
? <b>version</b> 0.1.0
? <b>Please write a short description about the block</b> A VIP for PIO
? <b>Block type</b> component
? <b>Source type</b> Verilog
</pre>

import ports
```bash
cat rtl/verilog/loopback/loopback.sv | duh-import-verilog-ports loopback.json5
```

add fileSets
```javascript
fileSets: {
    VerilogFiles: ['loopback.sv']
}
```

add parameter schema
```javascript
pSchema: {
    type: 'object',
    properties: {
        pioWidth: {
            title: 'Number of IO pads',
            type: 'integer', minimum: 1, maximum: 32, default: 32
        }
    }
}
```

By default, `duh init` will set the `library` field of the initialized DUH
component to `'blocks'`. However, since this is a VIP we should set the field
to `vip` instead.

```diff
  component: {
    vendor: 'sifive',
-    library: 'blocks',
+    library: 'vip',
    name: 'loopback',
    version: '0.1.0',
```

### Validation
To validate that our DUH document conforms to the DUH schema run
```bash
duh validate pio.json5
```


## Scala integration
In order to integrate our IP block into the Craft framework we need create a
Scala wrapper for our block that is parametrizeable and can be automatically
attached to an SoC. This is done by defining functions that instantiate and
connect Diplomatic nodes to the SoC. This section describes how to generate
boilerplate Scala code from a DUH document and what modifications need to be
made in order to fully integrate the PIO block.

### Generating Scala code with DUH
The best way to onboard a Verilog IP block is to use `duh-export-scala` to
generate an extensible Scala wrapper. We follow the convention of putting Scala
code in the `craft/${design}/src` directory. Run the following commands to
generate the Scala wrapper files.
```bash
duh-export-scala pio.json5 -o craft/pio/src
duh-export-scala loopback.json5 -o craft/loopback/src
```

This will create `${name}.scala` and `${name}-base.scala` files in the
`craft/pio/src` and `craft/loopback/src` directory. The `*-base.scala` files
contain base class definitions and should not be edited. Any changes to
`*-base.scala` files will be overwritten whenever `duh-export-scala` is rerun.

The `${name}.scala` files contain classes that extend the base classes defined
in `${name}-base.scala` and can be modified to override or augment the
base class functionality.

For a more detailed explanation of the generated Scala see the
[duh-scala README](https://github.com/sifive/duh-scala#blackbox-wrapper-api).


### Extending the `duh` generated base classes
By default, ports that are not associated with a bus interface are left
unconnected. We need to add the following changes we to the generated base
classes to create top-level ports for the `oenable`, `odata`, and `idata`
signals of the PIO block and connect them to the loopback VIP.

First we need to import classes from our loopback Scala wrapper into
`pio.scala`. Add the following line to the list of imports at the top of
`pio.scala`.
```scala
import sifive.vip.loopback._
```

We also need to define a bundle for the top-level ports that we want our block
to have. Add the following lines after the imports in the `pio.scala` file.
```scala
class NpioTopIO(
  val pioWidth: Int
) extends Bundle {
  val odata = Output(UInt(pioWidth.W))
  val oenable = Output(UInt(pioWidth.W))
  val idata = Input(UInt(pioWidth.W))
}
```

Then edit the `NpioTop` class to create a `BundleBridge` for the ports that we
want to route to the top. A `BundleBridge` is a diplomatic node that allows
`LazyModules` to connect arbitrary bundles to each other. We also need to
connect the clock and reset signals of our block to the system clock and reset.
Copy the following lines into the body of the `NpioTop`class in `pio.scala`.
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
    ioBridgeSink.bundle.reset_n := !(reset.asBool)

    // connect ioBridge source and sink
    ioBridgeSource.bundle.odata   := ioBridgeSink.bundle.odata
    ioBridgeSource.bundle.oenable := ioBridgeSink.bundle.oenable
    ioBridgeSink.bundle.idata     := ioBridgeSource.bundle.idata
  }
```

An important artifact of the Scala generator is the Object Model JSON. This
JSON file contains important information about the generated instance of the
design. To ensure our IP block is fully integrated with our tools we need to
populate the section of Object Model that describes our PIO block.

By default the generated Scala will populate the `OMMemoryRegion`s of attached
bus interfaces and `OMInterrupts` of our block's interrupt bus interfaces. We
need to add a field describing the `pioWidth` parameter of our block and also
associate an `OMRegisterMap` with the AXI4 memory region.

First we need to define a case class containing fields that we want to add to
the Object Model. Add the following case class definition to `pio.scala`
```scala
case class OMPIO(width: Int)
```

Then override the `userOM` and `getOMMemoryRegions` methods of `NpioTop` to add
our custom Object Model and add register maps to our block's memory regions
respectively.
```scala
  // add in custom fields to the Object Model entry for this block
  override val userOM: OMPIO = OMPIO(c.blackbox.pioWidth)

  // associate register maps with memory regions in Object model
  override def getOMMemoryRegions(resourceBindings: ResourceBindings) = {
    super.getOMMemoryRegions(resourceBindings).zip(omRegisterMaps).map { case (memRegion, regmap) =>
      memRegion.copy(registerMap = Some(regmap))
    }
  }
```

Finally, we need to edit `attach` method in the `NpioTop` companion object to
instantiate the loopback VIP in the test harness and connect it to the
`oenable`, `odata`, and `idata` ports of the PIO block. `bap.testHarness` is a
`LazyScope` which means that when you apply it to a code block
(i.e. `bap.testHarness { $my_code_block}`) that code block will execute as if
it were part of the `bap.testHarness` module. Replace the line
`// User code here` in the `attach` method of the `NpioTop` companion object
with the following lines.
```scala
    implicit val p: Parameters = bap.p

    // instantiate and connect the loopback vip in the test harness
    bap.testHarness {
      // instantiate the loopback vip
      val loopback = LazyModule(new NloopbackTop(NloopbackTopParams(
        blackbox = loopbackParams(
          pioWidth = c.blackbox.pioWidth,
          cacheBlockBytes = p(CacheBlockBytes)))))

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


## Wake integration
Wake is the build tool that the SiFive IP onboarding flow uses to run tests,
build software, generate documentation, and more. Look
[here](https://github.com/sifive/wake/blob/master/share/doc/wake/tutorial.md)
for a detailed tutorial on Wake. We follow the convention of putting Wake build
files in the `$package/build-rules/wake` directory.

In `block-pio-sifive/build-rules/wake/pio.wake` declare a variable that points
to the root of the `block-pio-sifive` package. Use this variable when
constructing any paths to files within `block-pio-sifive`. This is important
for ensuring that our Wake build rules are agnostic to the location of the
`block-pio-sifive` package.
```wake
global def blockPIOSiFiveRoot = simplify "{here}/../.."
```

### Creating a Wake `ScalaModule`
For each of our Scala projects, we need to define a Wake `ScalaModule` to
describe how it should be built. The framework we use to build Scala projects
is [api-scala-sifive](https://github.com/sifive/api-scala-sifive). Read the
README for more detailed documentation.

The loopback block uses components from
[soc-testsocket-sifive](https://github.com/sifive/soc-testsocket-sifive) and
[sifive-blocks](https://github.com/sifive/sifive-blocks) so we need to add the
already defined `sifiveBlocksScalaModule` and `sifiveSkeletonScalaModule` as
dependencies. Add this definition to `block-pio-sifive/build-rules/wake/pio.wake` to define the
loopback `ScalaModule`.
```wake
global def loopbackScalaModule =
  def name = "loopback"
  def path = "{blockPIOSiFiveRoot}/craft/loopback"
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
dependency here. Copy the following into `pio.wake`
```wake
global def pioScalaModule =
  def name = "pio"
  def rootDir = "{blockPIOSiFiveRoot}/craft/pio"
  def scalaVersion = sifiveSkeletonScalaModule.getScalaModuleScalaVersion
  def deps = loopbackScalaModule, sifiveBlocksScalaModule, sifiveSkeletonScalaModule, Nil
  makeScalaModule name rootDir scalaVersion
  | setScalaModuleSourceDirs ("src", Nil)
  | setScalaModuleDeps deps
  | setScalaModuleScalacOptions ("-Xsource:2.11", Nil)
```

### Creating a Wake `ScalaBlock`
Now that we have a Scala project we can create a `ScalaBlock`. A `ScalaBlock`
is just a `ScalaModule` associated with a config. Use `makeScalaBlock` to
create a `ScalaBlock`.

`duh` generates a config called
`sifive.blocks.${moduleName}.With${moduleName}Top`. Including this config in
our DUT will instantiate the block in the subsystem using the `attach` function
defined by `duh`. Copy the following lines for creating the `pio` block into
`pio.wake`.
```wake
global def pioBlock =
  def scalaModule = pioScalaModule
  def config = "sifive.blocks.pio.WithpioTop"
  makeScalaBlock scalaModule config
```

### Adding simulation options hooks
Now we need to describe how to simulate our blocks.
There are two main publish/subscribe targets for adding simulation option hooks:
`dutSimCompileOptionsHooks` and `dutSimExecuteOptionsHooks` for compile and
runtime options respectively. These hooks are functions of the type
`DUT => Option (a => a)`. A `None` return value means that no modifications
are made. Otherwise the returned transformation on `a` is applied.

`dutSimCompileOptionsHooks` operate on `DUTSimCompileOptions`.
`DUTSimCompileOptions` is defined as follows:
```wake
tuple DUTSimCompileOptions =
  global IncludeDirs:    List String
  global Defines:        List NamedArg
  global SourceFiles:    List Path
  global Plusargs:       List NamedArg
```

An example hook might look like:
```wake
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

The `pio` block needs `pio.sv` and `loopback.sv` to be added to the list
of files to be compiled in the simulation so we need to add hooks to
`dutSimCompileOptionsHooks` to add those files. There is a helper function
`makeBlackBoxHook` that takes a name and a transform function and returns
a hook that will apply the transform is the blackbox of the same name is found
in the `DUT`. We can use this to tell the simulator about our pio/loopback
source files because `duh` will generate blackboxes of those modules for us.
Copy the following hook defninitions into `pio.wake`.
```wake
publish dutSimCompileOptionsHooks = pioHook, loopbackHook, Nil

def loopbackHook =
  def name = "loopback"
  def addSources = source "{blockPIOSiFiveRoot}/rtl/verilog/loopback/loopback.sv", _
  makeBlackBoxHook name (editDUTSimCompileOptionsSourceFiles addSources)

def pioHook =
  def name = "pio"
  def addSources = source "{blockPIOSiFiveRoot}/rtl/verilog/pio/pio.sv", _
  makeBlackBoxHook name (editDUTSimCompileOptionsSourceFiles addSources)
```

## Creating a C driver for the block

To make it easier to write portable software that interfaces with the PIO block,
we will create a C driver that provides an abstraction for accessing the PIO memory-mapped registers.
Then we will register the driver file with the Wake build rules
so that the driver will be automatically included when any software program is compiled
for hardware designs with a PIO block present.

### Autogenerating drivers

For the common case of providing C functions for reading and writing memory-mapped registers,
we can run a driver generator script (`generate_drivers.py`) provided by [api-generator-sifive.git](https://github.com/sifive/api-generator-sifive).
This script uses the register information described in the DUH document to automatically generate read and write functions for each register.

To run the script, you first need Python 3.7 installed.
Then you need to install the necessary Python dependencies.
Finally, you can invoke the script with the path to the DUH document to generate the driver files.
Run the following commands:

```sh
# Set API_GENERATOR_SIFIVE to the path to the api-generator-sifive directory.
API_GENERATOR_SIFIVE=<path-to-api-generator-sifive>

# Create a Python virtualenv in the current directory
python3.7 -m venv .venv

# Install Python dependencies into virtual environment.
./.venv/bin/pip install pipenv==v2018.10.13
(. ./.venv/bin/activate && \
  PIPENV_PIPFILE=$API_GENERATOR_SIFIVE/scripts/generate_drivers_env/Pipfile pipenv sync)

./.venv/bin/python \
  $API_GENERATOR_SIFIVE/scripts/generate_drivers.py \
  --duh-document=pio.json5 \
  --vendor=sifive \
  --device=pio \
  --metal-dir=drivers/metal
```

This should produce a C header file at `drivers/metal/pio/sifive_pio0.h` and
C source file in `drivers/metal/sifive_pio.c`.

These driver files are just a base starting point,
and depending on your specific block, you may want to make modifications to them.

For the PIO block, we actually want to provide a very different C interface than the ones generated by the script above.
The script normally generates functions for each register, assuming a fixed width for each register.
Because the PIO's registers are variable-width depending on the width of the PIO hardware,
we will instead define a driver that exposes function for reading and writing an individual PIO pin given its index.

For this tutorial, you can copy the PIO driver files defined on the `master` branch at `drivers/metal`:

```sh
git checkout master -- drivers/metal
```

### Registering a driver with Wake

Driver files may be registered with the build rules so that when compiling software,
we can automatically include software drivers that match the hardware devices present in a given design.
This automatic driver selection is based on the Device Tree [`compatible`](https://elinux.org/Device_Tree_Usage#Understanding_the_compatible_Property) property.

If you followed the instructions above for generating the Scala wrapper classes,
it should have automatically generated a Device Tree `compatible` property with the value `sifive,pio-0.1.0`.
This value is based on the data provided in the DUH document.

To register the driver with Wake, you need to publish to the `driverImplementations` topic.
This topic should receive a `DriverImplementation` tuple,
which captures not only the paths to the header and source files,
but also the Device Tree property corresponding to the device.

Add the following lines to `block-pio-sifive/wake/pio.wake`:

```wake
def pioDriver =
  def sourceFiles =
    (sources "{blockPIOSiFiveRoot}/drivers/metal" `.*.c`)
    ++ (sources "{blockPIOSiFiveRoot}/drivers/metal" `.*.h`)

  def compatibleStrings = "sifive,pio-0.1.0", Nil
  def vendor = "sifive"
  def deviceName = "pio"
  def cFiles = sourceFiles
  def includeDirs = "{blockPIOSiFiveRoot}/drivers/metal", Nil
  def visibleFiles = sourceFiles

  makeDriverImplementation
  compatibleStrings
  vendor
  deviceName
  cFiles
  includeDirs
  visibleFiles

publish driverImplementations = pioDriver, Nil
```


## Making a test

Currently, only C integration tests are supported. A simple test is included
with this repository in `block-pio-sifive/tests/c/demo/main.c`. It looks like this.

```c
#include <pio/sifive_pio0.h>

int main()
{
  // read/write to axi block
  const struct metal_pio *m_pio = get_metal_pio(0);
  bool rv;

  // Check read/write of individual pin
  metal_pio_odata_write(m_pio, 0, 1);
  metal_pio_oenable_write(m_pio, 0, 1);
  rv = metal_pio_idata_read(m_pio, 0);
  if (rv)
    return 1;
  return 0;
}
```

To tell wake how to compile this test we need to create a `TestProgramPlan`.
`TestProgramPlan` is defined as follows:
```wake
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
```wake
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
`block-pio-sifive/build-rules/wake/pio.wake` to create a `TestProgramPlan` for `block-pio-sifive/tests/c/demo/main.c`.
```wake
global def demo =
  def programName = "demo"
  def cFiles = source "{blockPIOSiFiveRoot}/tests/c/demo/main.c", Nil
  makeTestProgramPlan programName cFiles
```

Suppose we did not want to hardcode the pin number being tested and we edited
our test to look like this.
```c
#include <pio/sifive_pio0.h>

int main()
{
  // read/write to axi block
  const struct metal_pio *m_pio = get_metal_pio(0);
  bool rv;

  // Check read/write of individual pin
  metal_pio_odata_write(m_pio, PIO_PIN, 1);
  metal_pio_oenable_write(m_pio, PIO_PIN, 1);
  rv = metal_pio_idata_read(m_pio, PIO_PIN);
  if (rv)
    return 1;
  return 0;
}
```

We would need to add an argument to the C compilation. Our new wake program would
then look like this.
```wake
global def demo =
  def programName = "demo"
  def cFiles = source "{blockPIOSiFiveRoot}/tests/c/demo/main.c", Nil
  makeTestProgramPlan programName cFiles
  | editTestProgramPlanCFlags ("-DPIO_PIN=1", _)
```

A more complicated program like dhrystone looks like this.
```wake
def dhrystone =
  def programName = "dhrystone"
  def prefix = "{blockPIOSiFiveRoot}/tests/c/dhrystone"
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
[api-generator-sifive](https://github.com/sifive/api-generator-sifive) includes a `DUT`
constructor called `rocketChipDUTMaker` that takes a `RocketChipDUTPlan` and
returns a `DUT`. To construct a `DUT` to test our block we can use the helper
function `makeTestSocketDUT {name} {blocks}`.

`makeTestSocketDUT` constructs a `RocketChipDUTPlan` that includes a simulation
uart, a test-finisher, and the extra blocks supplied by the `blocks` argument.

Copy the following lines for creating a test `DUT` for the pio block into
`block-pio-sifive/build-rules/wake/pio.wake`.
```wake
global def pioDUT =
  def name = "pioDUT"
  def blocks = pioBlock, Nil
  makeTestSocketDUT name blocks
```

## Making and publishing a test
A wake `DUTTest` combines all the necessary components for compiling a design and
simulating a test program running on that design. Use `makeDUTTest` to create a
`DUTTest`. It has the following type signature.
```wake
publish test =
  def name = ${name of test} # String
  def filter = ${returns True only for DUTs where this test is applicable} # DUT => Boolean
  def program = ${test program to run} # Program
  def bootloader = ${bootloader to load the test program} # SimBootloader
  def plusargs = ${extra plusargs to use for simulation} # List NamedArg
  makeDUTTest name filter program bootloader plusargs
```

The `makeOMBlockTest` is a helper function that provides a default `filter` and
`bootloader` for tests associated with a `ScalaBlock`. It has the following type
signature.
```wake
def myBlockTest =
  def name = ${name of the test} # String
  def deviceType = ${the Object Model type of the block} # String
  def program = ${test program to run} # Program
  def plusargs = ${extra plusargs to use for simulation} # List NamedArg
  makeOMBlockTest name deviceType program plusargs =
```

Copy the following lines into `block-pio-sifive/build-rules/wake/pio.wake` to create and publish the demo
test for the pio block. Publishing to `dutTests` will register this test so that it
is automatically run whenever an applicable `DUT` is being tested (see next
section).
```wake
publish dutTests = demoPioTest, Nil

global def demoPioTest =
  def name = "demo"
  def type = "OMpio"
  def program = demo
  def plusargs =
    NamedArg        "verbose",
    NamedArgInteger "random_seed"      1234,
    NamedArgInteger "tilelink_timeout" 16000,
    NamedArgInteger "max-cycles"       50000,
    Nil
  makeOMBlockTest name type program plusargs
```

The `verbose` plusarg toggles printing of the instruction trace to stderr. The
stdout, stderr, output of `printf`, and waveform files will be output to the
simulation result directory.

Now that we've published a test we can run it on `pioDUT` with the command
```bash
wake -v runSim pioDUT
```

Verilator is the default simulator. If you would like to run on a different
simulator you can use the command.
```bash
wake -v runSimWith pioDUT [VCS|VCS_Waves|Verilator|Verilator_Waves|Xcelium|Xcelium_Waves]
```


## Parameterizing your block
Because the PIO block is parameterizeable we need to make sure we test multiple
configurations of the block. To create another parameterization of the PIO
block, add another config to `pio.scala`. The following config will instantiate
the PIO at base address 0x70000 and with a width of 16

```scala
class WithpioTop2 extends Config((site, here, up) => {
  case BlockDescriptorKey =>
    val defaults = NpioTopParams.defaults(
        ctrl_base = 0x70000L,
        cacheBlockBytes = site(CacheBlockBytes))
    BlockDescriptor(
      name = "pio",
      place = NpioTop.attach(
        defaults.copy(blackbox = defaults.blackbox.copy(pioWidth = 16)))
    ) +: up(BlockDescriptorKey, site)
})
```

Now we need to add a new block to `pio.wake` and add the block to a DUT.
```wake
global def pio16Block =
  setScalaBlockConfig "sifive.blocks.pio.WithpioTop2" pioBlock

global def pio16DUT =
  def name = "pio16DUT"
  def blocks = pio16Block, Nil
  makeTestSocketDUT name blocks
```

We should be able to reuse the test and driver for our default PIO block since
the headers are generated based on the Object Model.

## Documenting your block
To create Scribble documentation for the new block, we will need to create three sections of text:
 - an "Overview" paragraph with a brief description of the block,
 - a "Programming" chapter describing how to program and configure the block, and
 - a "Hardware Interface" chapter describing how to interface to the block.

Scribble will include these sections in documents and manuals for designs which include the block.

### Creating the documentation directories.
To begin, create a directory to hold the `pio` documentation.  The directory structure allows multiple blocks to be documented,
but in our case we are only describing `pio`.
```
mkdir -p docs/scribble/components/pio
```
In addition, edit the project's wake file to include the new scribble directory in the documentation path.
Specifically, add the following line to `pio.wake`.
```wake
publish scribbleDirectories = "{blockPIOSiFiveRoot}/docs/scribble", Nil
```

### Creating an Onboarding document.
At this point, we can create the first draft of the PIO Onboarding document. Type the following command.
```
wake makeOnboardingDocument pioDUT
```
The command creates two files, `pioDUT.html` and `pioDut.adoc`, both in the directory `build/api-generator-sifive/pioDUT/documentation`.
The .html file can be viewed directly in a web browser, and the .adoc file contains AsciiDoc which can be used for further processing,
See the [AsciiDoctor PDF project](https://asciidoctor.org/docs/asciidoctor-pdf) for information on converting AsciiDoc to PDF.

Note we have created a PIO Onboarding document, even though we haven't written anything about the PIO block.
This initial Onboarding document includes placeholder sections which fill in the missing PIO sections.
We can create an Onboarding document at any time, even if we haven't written documentation yet.

The next step replaces those placeholder sections with text specific to the Parallel I/O block.
We will create three Jinja templates `Overview.jinja2`, `Programming.jinja2` and `HardwareInterface.jinja2`,
all of which reside in the `docs/scribble/components/pio` directory.

#### Create the Overview template.
The Overview template contains a simple paragraph describing what the block does.
While the pioDUT configuration has only a single instance of PIO,
the paragraph should be crafted to allow for multiple instances.

To get started, copy the following text into the file `Overview.jinja2`.
```
= Parallel I/O (PIO)

The {{ product_name }} contains {{ devices|length|english_number }} parallel I/O (PIO) block{{ devices|pluralize }} for simple parallel input/output.
The PIO block is further described in <<chapter-pio>>.
```
The functions and variables used in this template are described in [Scribble Test Socket](https://github.com/sifive/scribble-testsocket-sifive).
This is a good time rerun the wake command and update the document. (`wake makeOnboardingDocument pioDUT`)
You should see an introductory paragraph:
```
The pioDUT Test Socket contains one parallel I/O (PIO) block for simple parallel input/output.
The PIO block is further described in Parallel I/O (PIO).
```

#### Create the Programming template.
The Programming chapter goes into more detail about what the block does and how to use it.
For PIO, it should give a general description of a Parallel I/O device and provide information on each instance in the design.
If the block has registers, this chapter should invoke `RegisterMap`
to display a register map along with descriptions of all the register fields.
(See [Scribble Test Socket](https://github.com/sifive/scribble-testsocket-sifive) for more information on `RegisterMap`.)

For pioDUT, we are going to create the main `Programming` section which invokes `RegisterMap`,
and we're going to create a subsection called `InstanceTable` which displays the configurable values of each PIO instance.

First, copy the following text into the main section `Programming.jinja2`.
```
{% set registers = RegisterMap(scope) %}
[[chapter-pio]]
= Parallel I/O (PIO)

This chapter describes the operation of the SiFive parallel I/O device.

== PIO Overview

The PIO device provides a series of I/O pins that can be read or written via memory mapped registers.
Pins may be independently configured as either input or output pins via the `OENABLE` register.

== PIO Instances in {{  product_name }}
{{ product_name }} contains {{ devices|length|english_number }} PIO instance{{ devices|pluralize('s') }}.
{{ devices|pluralize('Its address,Their addresses') }} and data width{{ devices|pluralize }} are shown below in <<table-pio-instances>>.

{{ Section(".InstanceTable", scope, reference_id="table-pio-instances", **kwargs) }}


== PIO Registers
The register map for the PIO control registers is shown in <<table-pio-register-map>>.
The PIO device is designed to work with naturally aligned 32-bit memory accesses.

{{ registers.table("PIO Register Map", "table-pio-register-map") }}


== PIO Output Register (`ODATA`)
{{ registers.fields("ODATA") }}


== PIO Input Enable Register (`OENABLE`)
{{ registers.fields("OENABLE") }}


== PIO Input Register (`IDATA`)
{{ registers.fields("IDATA") }}
```

Second, copy the following text into the subsection `InstanceTable.jinja2`.
```
[[{{ reference_id }}]]
.Parallel I/O Instances
[cols="1,1,2",options="header"]
|===
| Address | Data Width | Description

{% for pio in devices %}
| {{ base_address(pio) }} | {{ pio.width }} | {{  pio.description or "" }}
{%  endfor %}
|===
```

When both files have been created, re-run the `wake makeOnboardingDocument pioDUT` command and verify the document
contains the new "Parallel I/O (PIO)" chapter.

#### Create the Hardware Interface template.
For the Hardware Interface chapter, we will create both a Jinja template and a diagram.
Both files will be located in the same PIO documentation directory as the other sections.

First, install the file `pio-diagram.svg` into `docs/scribble/components/pio`.
The easiest way is to checkout the file from the `master` branch.
```
cd docs/scribble/components/pio
git checkout master -- pio-diagram.svg
```

Second, copy the following text into the file `HardwareInterface.jinja2`.
```
[[chapter-pio-interfaces]]
= Parallel I/O (PIO) Interfaces

The PIO device may be configured to support up to 32 inputs/output pins.
Each instance has three ports:

- `odata`, containing output data
- `oenable`, selecting between input and output modes
- `idata`, receiving input data

The three signals are assumed to be connected to tri-state buffers such that each `odata` and `idata` signal is connected to a single external pin.

{{ Figure("{here}/pio-diagram.svg", title="PIO Block Diagram", id="pio-diagram", width="50%", **context) }}
```

And finally, re-run `wake makeOnboardingDocument pioDUT`.  We should now have a complete document.
