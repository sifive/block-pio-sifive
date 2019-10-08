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
* [Creating the DUH document](#creating-the-duh-document)
* [Scala integration](#scala-integration)

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
npm i duh@1.15.0
```

This will create a `node_modules` subdirectory in your current directory with
all the package sources. The `duh` executable along with other useful tools are
installed in `node_modules/.bin`. You may want to add this to your path with
`export PATH=./node_modules/.bin:$PATH` for convenience.

### Initializing the DUH document
To create an initial DUH document run `duh init` and answer the prompts. For
this tutorial we will name the block `pio` and write the DUH document to
`block-pio-sifive/pio.json5`
<pre>
duh init
? <b>Document file name</b> pio.json5
? <b>Block name</b> pio
? <b>version</b> 0.1.0
? <b>Please write a short description about the block</b> A Parallel IO block
? <b>Block type</b> component
? <b>Source type</b> Verilog
</pre>

Since we are onboarding a Verilog IP block we should set the `fileSets` field in
our DUH component to
```javascript
fileSets: [{
    VerilogFiles: ['pio.sv']
}]
```

### Importing verilog ports
Fill in the port definitions of the block in the DUH document. The
`duh-import-verilog-ports` tool can parse the verilog and fill in the
definitions for you.
```bash
cat rtl/pio/pio.sv | duh-import-verilog-ports pio.json5
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
            fields: [{name: 'data', bits: 32}]
        }, {
            name: 'OENABLE', addressOffset: 32, size: 32,
            displayName: 'Data direction',
            description: 'determines whether the pin is an input or an output. If the data direction bit is a 1, then the pin is an input',
            fields: [{name: 'data', bits: 32}]
        }, {
            name: 'IDATA', addressOffset: 64, size: 32,
            displayName: 'Input data',
            description: 'read the port pins',
            fields: [{name: 'data', bits: 32}]
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
cat rtl/loopback/loopback.sv | duh-import-verilog-ports loopback.json5
```

add fileSets
```javascript
fileSets: [{
    VerilogFiles: ['loopback.sv']
}]
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
