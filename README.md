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
            fields: [{name: 'data', bitOffset: 0, bitWidth: 32}]
        }, {
            name: 'OENABLE', addressOffset: 32, size: 32,
            displayName: 'Data direction',
            description: 'determines whether the pin is an input or an output. If the data direction bit is a 1, then the pin is an input',
            fields: [{name: 'data', bitOffset: 0, bitWidth: 32}]
        }, {
            name: 'IDATA', addressOffset: 64, size: 32,
            displayName: 'Input data',
            description: 'read the port pins',
            fields: [{name: 'data', bitOffset: 0, bitWidth: 32}]
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
