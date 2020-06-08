// Generated Code
// Please DO NOT EDIT


package sifive.blocks.pio

import chisel3._
// import chisel3.{withClockAndReset, _}
import chisel3.util._
import chisel3.experimental._

import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.diplomaticobjectmodel.{DiplomaticObjectModelAddressing, HasLogicalTreeNode}
import freechips.rocketchip.diplomaticobjectmodel.logicaltree.{LogicalTreeNode, LogicalModuleTree}
import freechips.rocketchip.diplomaticobjectmodel.model._
import freechips.rocketchip.amba.axi4._
import freechips.rocketchip.amba.apb._
import freechips.rocketchip.amba.ahb._
import freechips.rocketchip.interrupts._
import freechips.rocketchip.util.{ElaborationArtefacts}
import freechips.rocketchip.tilelink._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.regmapper._

import sifive.skeleton._
import sifive.blocks.util.{NonBlockingEnqueue, NonBlockingDequeue}



class pioBlackBoxIO(
  val addrWidth: Int,
  val dataWidth: Int,
  val pioWidth: Int,
  val writeStrobeWidth: Int
) extends Bundle {
  val t_ctrl_awvalid = Input(Bool())
  val t_ctrl_awready = Output(Bool())
  val t_ctrl_awaddr = Input(UInt((addrWidth).W))
  val t_ctrl_awprot = Input(UInt((3).W))
  val t_ctrl_wvalid = Input(Bool())
  val t_ctrl_wready = Output(Bool())
  val t_ctrl_wdata = Input(UInt((dataWidth).W))
  val t_ctrl_wstrb = Input(UInt((writeStrobeWidth).W))
  val t_ctrl_bvalid = Output(Bool())
  val t_ctrl_bready = Input(Bool())
  val t_ctrl_bresp = Output(UInt((2).W))
  val t_ctrl_arvalid = Input(Bool())
  val t_ctrl_arready = Output(Bool())
  val t_ctrl_araddr = Input(UInt((addrWidth).W))
  val t_ctrl_arprot = Input(UInt((3).W))
  val t_ctrl_rvalid = Output(Bool())
  val t_ctrl_rready = Input(Bool())
  val t_ctrl_rdata = Output(UInt(((dataWidth)).W))
  val t_ctrl_rresp = Output(UInt((2).W))
  val t_sideband_awvalid = Input(Bool())
  val t_sideband_awready = Output(Bool())
  val t_sideband_awaddr = Input(UInt((addrWidth).W))
  val t_sideband_awprot = Input(UInt((3).W))
  val t_sideband_wvalid = Input(Bool())
  val t_sideband_wready = Output(Bool())
  val t_sideband_wdata = Input(UInt((dataWidth).W))
  val t_sideband_wstrb = Input(UInt((writeStrobeWidth).W))
  val t_sideband_bvalid = Output(Bool())
  val t_sideband_bready = Input(Bool())
  val t_sideband_bresp = Output(UInt((2).W))
  val t_sideband_arvalid = Input(Bool())
  val t_sideband_arready = Output(Bool())
  val t_sideband_araddr = Input(UInt((addrWidth).W))
  val t_sideband_arprot = Input(UInt((3).W))
  val t_sideband_rvalid = Output(Bool())
  val t_sideband_rready = Input(Bool())
  val t_sideband_rdata = Output(UInt(((dataWidth)).W))
  val t_sideband_rresp = Output(UInt((2).W))
  val t_mem_awvalid = Input(Bool())
  val t_mem_awready = Output(Bool())
  val t_mem_awaddr = Input(UInt((addrWidth).W))
  val t_mem_awprot = Input(UInt((3).W))
  val t_mem_wvalid = Input(Bool())
  val t_mem_wready = Output(Bool())
  val t_mem_wdata = Input(UInt((dataWidth).W))
  val t_mem_wstrb = Input(UInt((writeStrobeWidth).W))
  val t_mem_bvalid = Output(Bool())
  val t_mem_bready = Input(Bool())
  val t_mem_bresp = Output(UInt((2).W))
  val t_mem_arvalid = Input(Bool())
  val t_mem_arready = Output(Bool())
  val t_mem_araddr = Input(UInt((addrWidth).W))
  val t_mem_arprot = Input(UInt((3).W))
  val t_mem_rvalid = Output(Bool())
  val t_mem_rready = Input(Bool())
  val t_mem_rdata = Output(UInt(((dataWidth)).W))
  val t_mem_rresp = Output(UInt((2).W))
  val irq0 = Output(Bool())
  val irq1 = Output(Bool())
  val odata = Output(UInt(((pioWidth)).W))
  val oenable = Output(UInt(((pioWidth)).W))
  val idata = Input(UInt((pioWidth).W))
  val clk = Input(Bool())
  val reset_n = Input(Bool())
}

class pio(
  val addrWidth: Int,
  val dataWidth: Int,
  val pioWidth: Int,
  val writeStrobeWidth: Int
) extends BlackBox(Map(
  "addrWidth" -> core.IntParam(addrWidth),
  "dataWidth" -> core.IntParam(dataWidth),
  "pioWidth" -> core.IntParam(pioWidth),
  "writeStrobeWidth" -> core.IntParam(writeStrobeWidth)
)) with HasBlackBoxResource {
  val io = IO(new pioBlackBoxIO(
    addrWidth,
    dataWidth,
    pioWidth,
    writeStrobeWidth
  ))
// setResource("top.v")
}

case class pioParams(
  addrWidth: Int = 12,
  dataWidth: Int = 32,
  pioWidth: Int = 32,
  writeStrobeWidth: Int = 4,
  t_sidebandParams: Pt_sidebandParams,
  t_ctrlParams: Pt_ctrlParams,
  t_memParams: Pt_memParams,
  irqParams: PirqParams,
  cacheBlockBytes: Int
)

// busType: AXI4-Lite, mode: slave
// busType: AXI4-Lite, mode: slave
// busType: AXI4-Lite, mode: slave
// busType: interrupts, mode: master

class LpioBase(c: pioParams)(implicit p: Parameters) extends LazyModule {

  def extraResources(resources: ResourceBindings) = Map[String, Seq[ResourceValue]]()

  val device = new SimpleDevice("pio", Seq("sifive,pio-0.1.0")) {
    override def describe(resources: ResourceBindings): Description = {
      val Description(name, mapping) = super.describe(resources)
      Description(name, mapping ++ extraResources(resources))
    }
  }

  val addrWidth = c.addrWidth
  val dataWidth = c.dataWidth
  val pioWidth = c.pioWidth
  val writeStrobeWidth = c.writeStrobeWidth

  val t_sidebandNode = AXI4SlaveNode(Seq(
    AXI4SlavePortParameters(
      slaves = Seq(
        AXI4SlaveParameters(
          address       = List(AddressSet(c.t_sidebandParams.base, ((1L << addrWidth) - 1))),
          executable    = c.t_sidebandParams.executable,
          supportsWrite = TransferSizes(1, ((dataWidth) * 1 / 8)),
          supportsRead  = TransferSizes(1, ((dataWidth) * 1 / 8)),
          interleavedId = Some(0),
          resources     = device.reg("SIDEBAND_CSR")
        )
      ),
      beatBytes = (dataWidth) / 8
    )
  ))

  val t_ctrlNode = AXI4SlaveNode(Seq(
    AXI4SlavePortParameters(
      slaves = Seq(
        AXI4SlaveParameters(
          address       = List(AddressSet(c.t_ctrlParams.base, ((1L << addrWidth) - 1))),
          executable    = c.t_ctrlParams.executable,
          supportsWrite = TransferSizes(1, ((dataWidth) * 1 / 8)),
          supportsRead  = TransferSizes(1, ((dataWidth) * 1 / 8)),
          interleavedId = Some(0),
          resources     = device.reg("CSR")
        )
      ),
      beatBytes = (dataWidth) / 8
    )
  ))

  val irqNode = IntSourceNode(IntSourcePortSimple(
    num = 2,
    resources = device.int
  ))

  val t_memNode = AXI4SlaveNode(Seq(
    AXI4SlavePortParameters(
      slaves = Seq(
        AXI4SlaveParameters(
          address       = List(AddressSet(c.t_memParams.base, ((1L << addrWidth) - 1))),
          executable    = c.t_memParams.executable,
          supportsWrite = TransferSizes(1, ((dataWidth) * 1 / 8)),
          supportsRead  = TransferSizes(1, ((dataWidth) * 1 / 8)),
          interleavedId = Some(0),
          resources     = device.reg("JustMemoryNoRegisters")
        )
      ),
      beatBytes = (dataWidth) / 8
    )
  ))


  val ioBridgeSource = BundleBridgeSource(() => new pioBlackBoxIO(
    c.addrWidth,
    c.dataWidth,
    c.pioWidth,
    c.writeStrobeWidth
  ))

  class LpioBaseImp extends LazyRawModuleImp(this) {
    val blackbox = Module(new pio(
      c.addrWidth,
      c.dataWidth,
      c.pioWidth,
      c.writeStrobeWidth
    ))
    // interface wiring 2



    // port wiring
    blackbox.io.t_ctrl_awvalid := ioBridgeSource.bundle.t_ctrl_awvalid
    ioBridgeSource.bundle.t_ctrl_awready := blackbox.io.t_ctrl_awready
    blackbox.io.t_ctrl_awaddr := ioBridgeSource.bundle.t_ctrl_awaddr
    blackbox.io.t_ctrl_awprot := ioBridgeSource.bundle.t_ctrl_awprot
    blackbox.io.t_ctrl_wvalid := ioBridgeSource.bundle.t_ctrl_wvalid
    ioBridgeSource.bundle.t_ctrl_wready := blackbox.io.t_ctrl_wready
    blackbox.io.t_ctrl_wdata := ioBridgeSource.bundle.t_ctrl_wdata
    blackbox.io.t_ctrl_wstrb := ioBridgeSource.bundle.t_ctrl_wstrb
    ioBridgeSource.bundle.t_ctrl_bvalid := blackbox.io.t_ctrl_bvalid
    blackbox.io.t_ctrl_bready := ioBridgeSource.bundle.t_ctrl_bready
    ioBridgeSource.bundle.t_ctrl_bresp := blackbox.io.t_ctrl_bresp
    blackbox.io.t_ctrl_arvalid := ioBridgeSource.bundle.t_ctrl_arvalid
    ioBridgeSource.bundle.t_ctrl_arready := blackbox.io.t_ctrl_arready
    blackbox.io.t_ctrl_araddr := ioBridgeSource.bundle.t_ctrl_araddr
    blackbox.io.t_ctrl_arprot := ioBridgeSource.bundle.t_ctrl_arprot
    ioBridgeSource.bundle.t_ctrl_rvalid := blackbox.io.t_ctrl_rvalid
    blackbox.io.t_ctrl_rready := ioBridgeSource.bundle.t_ctrl_rready
    ioBridgeSource.bundle.t_ctrl_rdata := blackbox.io.t_ctrl_rdata
    ioBridgeSource.bundle.t_ctrl_rresp := blackbox.io.t_ctrl_rresp
    blackbox.io.t_sideband_awvalid := ioBridgeSource.bundle.t_sideband_awvalid
    ioBridgeSource.bundle.t_sideband_awready := blackbox.io.t_sideband_awready
    blackbox.io.t_sideband_awaddr := ioBridgeSource.bundle.t_sideband_awaddr
    blackbox.io.t_sideband_awprot := ioBridgeSource.bundle.t_sideband_awprot
    blackbox.io.t_sideband_wvalid := ioBridgeSource.bundle.t_sideband_wvalid
    ioBridgeSource.bundle.t_sideband_wready := blackbox.io.t_sideband_wready
    blackbox.io.t_sideband_wdata := ioBridgeSource.bundle.t_sideband_wdata
    blackbox.io.t_sideband_wstrb := ioBridgeSource.bundle.t_sideband_wstrb
    ioBridgeSource.bundle.t_sideband_bvalid := blackbox.io.t_sideband_bvalid
    blackbox.io.t_sideband_bready := ioBridgeSource.bundle.t_sideband_bready
    ioBridgeSource.bundle.t_sideband_bresp := blackbox.io.t_sideband_bresp
    blackbox.io.t_sideband_arvalid := ioBridgeSource.bundle.t_sideband_arvalid
    ioBridgeSource.bundle.t_sideband_arready := blackbox.io.t_sideband_arready
    blackbox.io.t_sideband_araddr := ioBridgeSource.bundle.t_sideband_araddr
    blackbox.io.t_sideband_arprot := ioBridgeSource.bundle.t_sideband_arprot
    ioBridgeSource.bundle.t_sideband_rvalid := blackbox.io.t_sideband_rvalid
    blackbox.io.t_sideband_rready := ioBridgeSource.bundle.t_sideband_rready
    ioBridgeSource.bundle.t_sideband_rdata := blackbox.io.t_sideband_rdata
    ioBridgeSource.bundle.t_sideband_rresp := blackbox.io.t_sideband_rresp
    blackbox.io.t_mem_awvalid := ioBridgeSource.bundle.t_mem_awvalid
    ioBridgeSource.bundle.t_mem_awready := blackbox.io.t_mem_awready
    blackbox.io.t_mem_awaddr := ioBridgeSource.bundle.t_mem_awaddr
    blackbox.io.t_mem_awprot := ioBridgeSource.bundle.t_mem_awprot
    blackbox.io.t_mem_wvalid := ioBridgeSource.bundle.t_mem_wvalid
    ioBridgeSource.bundle.t_mem_wready := blackbox.io.t_mem_wready
    blackbox.io.t_mem_wdata := ioBridgeSource.bundle.t_mem_wdata
    blackbox.io.t_mem_wstrb := ioBridgeSource.bundle.t_mem_wstrb
    ioBridgeSource.bundle.t_mem_bvalid := blackbox.io.t_mem_bvalid
    blackbox.io.t_mem_bready := ioBridgeSource.bundle.t_mem_bready
    ioBridgeSource.bundle.t_mem_bresp := blackbox.io.t_mem_bresp
    blackbox.io.t_mem_arvalid := ioBridgeSource.bundle.t_mem_arvalid
    ioBridgeSource.bundle.t_mem_arready := blackbox.io.t_mem_arready
    blackbox.io.t_mem_araddr := ioBridgeSource.bundle.t_mem_araddr
    blackbox.io.t_mem_arprot := ioBridgeSource.bundle.t_mem_arprot
    ioBridgeSource.bundle.t_mem_rvalid := blackbox.io.t_mem_rvalid
    blackbox.io.t_mem_rready := ioBridgeSource.bundle.t_mem_rready
    ioBridgeSource.bundle.t_mem_rdata := blackbox.io.t_mem_rdata
    ioBridgeSource.bundle.t_mem_rresp := blackbox.io.t_mem_rresp
    ioBridgeSource.bundle.irq0 := blackbox.io.irq0
    ioBridgeSource.bundle.irq1 := blackbox.io.irq1
    ioBridgeSource.bundle.odata := blackbox.io.odata
    ioBridgeSource.bundle.oenable := blackbox.io.oenable
    blackbox.io.idata := ioBridgeSource.bundle.idata
    ioBridgeSource.bundle.irq0 := blackbox.io.irq0
    ioBridgeSource.bundle.irq1 := blackbox.io.irq1
    blackbox.io.clk := ioBridgeSource.bundle.clk
    blackbox.io.reset_n := ioBridgeSource.bundle.reset_n
    // interface alias
    val t_sideband0 = t_sidebandNode.in(0)._1
    val t_ctrl0 = t_ctrlNode.in(0)._1
    val t_mem0 = t_memNode.in(0)._1
    val irq0 = irqNode.out(0)._1
    // interface wiring
    // wiring for t_sideband of type AXI4-Lite
    // -> {"aw":{"valid":1,"ready":-1,"bits":{"id":"awIdWidth","addr":"awAddrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"w":{"valid":1,"ready":-1,"bits":{"data":"wDataWidth","strb":"wStrbWidth","last":1}},"b":{"valid":-1,"ready":1,"bits":{"id":"-bIdWidth","resp":-2}},"ar":{"valid":1,"ready":-1,"bits":{"id":"arIdWidth","addr":"addrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"r":{"valid":-1,"ready":1,"bits":{"id":"-rIdWidth","data":"-dataWidth","resp":-2,"last":-1}}}
    // aw
    blackbox.io.t_sideband_awvalid := t_sideband0.aw.valid
    t_sideband0.aw.ready := blackbox.io.t_sideband_awready
    // aw
    // AWID
    blackbox.io.t_sideband_awaddr := t_sideband0.aw.bits.addr
    // AWLEN
    // AWSIZE
    // AWBURST
    // AWLOCK
    // AWCACHE
    blackbox.io.t_sideband_awprot := t_sideband0.aw.bits.prot
    // AWQOS
    // AWREGION
    // w
    blackbox.io.t_sideband_wvalid := t_sideband0.w.valid
    t_sideband0.w.ready := blackbox.io.t_sideband_wready
    // w
    blackbox.io.t_sideband_wdata := t_sideband0.w.bits.data
    blackbox.io.t_sideband_wstrb := t_sideband0.w.bits.strb
    // WLAST
    // b
    t_sideband0.b.valid := blackbox.io.t_sideband_bvalid
    blackbox.io.t_sideband_bready := t_sideband0.b.ready
    // b
    t_sideband0.b.bits.id := 0.U // BID
    t_sideband0.b.bits.resp := blackbox.io.t_sideband_bresp
    // ar
    blackbox.io.t_sideband_arvalid := t_sideband0.ar.valid
    t_sideband0.ar.ready := blackbox.io.t_sideband_arready
    // ar
    // ARID
    blackbox.io.t_sideband_araddr := t_sideband0.ar.bits.addr
    // ARLEN
    // ARSIZE
    // ARBURST
    // ARLOCK
    // ARCACHE
    blackbox.io.t_sideband_arprot := t_sideband0.ar.bits.prot
    // ARQOS
    // ARREGION
    // r
    t_sideband0.r.valid := blackbox.io.t_sideband_rvalid
    blackbox.io.t_sideband_rready := t_sideband0.r.ready
    // r
    t_sideband0.r.bits.id := 0.U // RID
    t_sideband0.r.bits.data := blackbox.io.t_sideband_rdata
    t_sideband0.r.bits.resp := blackbox.io.t_sideband_rresp
    t_sideband0.r.bits.last := true.B // RLAST

    // wiring for t_ctrl of type AXI4-Lite
    // -> {"aw":{"valid":1,"ready":-1,"bits":{"id":"awIdWidth","addr":"awAddrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"w":{"valid":1,"ready":-1,"bits":{"data":"wDataWidth","strb":"wStrbWidth","last":1}},"b":{"valid":-1,"ready":1,"bits":{"id":"-bIdWidth","resp":-2}},"ar":{"valid":1,"ready":-1,"bits":{"id":"arIdWidth","addr":"addrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"r":{"valid":-1,"ready":1,"bits":{"id":"-rIdWidth","data":"-dataWidth","resp":-2,"last":-1}}}
    // aw
    blackbox.io.t_ctrl_awvalid := t_ctrl0.aw.valid
    t_ctrl0.aw.ready := blackbox.io.t_ctrl_awready
    // aw
    // AWID
    blackbox.io.t_ctrl_awaddr := t_ctrl0.aw.bits.addr
    // AWLEN
    // AWSIZE
    // AWBURST
    // AWLOCK
    // AWCACHE
    blackbox.io.t_ctrl_awprot := t_ctrl0.aw.bits.prot
    // AWQOS
    // AWREGION
    // w
    blackbox.io.t_ctrl_wvalid := t_ctrl0.w.valid
    t_ctrl0.w.ready := blackbox.io.t_ctrl_wready
    // w
    blackbox.io.t_ctrl_wdata := t_ctrl0.w.bits.data
    blackbox.io.t_ctrl_wstrb := t_ctrl0.w.bits.strb
    // WLAST
    // b
    t_ctrl0.b.valid := blackbox.io.t_ctrl_bvalid
    blackbox.io.t_ctrl_bready := t_ctrl0.b.ready
    // b
    t_ctrl0.b.bits.id := 0.U // BID
    t_ctrl0.b.bits.resp := blackbox.io.t_ctrl_bresp
    // ar
    blackbox.io.t_ctrl_arvalid := t_ctrl0.ar.valid
    t_ctrl0.ar.ready := blackbox.io.t_ctrl_arready
    // ar
    // ARID
    blackbox.io.t_ctrl_araddr := t_ctrl0.ar.bits.addr
    // ARLEN
    // ARSIZE
    // ARBURST
    // ARLOCK
    // ARCACHE
    blackbox.io.t_ctrl_arprot := t_ctrl0.ar.bits.prot
    // ARQOS
    // ARREGION
    // r
    t_ctrl0.r.valid := blackbox.io.t_ctrl_rvalid
    blackbox.io.t_ctrl_rready := t_ctrl0.r.ready
    // r
    t_ctrl0.r.bits.id := 0.U // RID
    t_ctrl0.r.bits.data := blackbox.io.t_ctrl_rdata
    t_ctrl0.r.bits.resp := blackbox.io.t_ctrl_rresp
    t_ctrl0.r.bits.last := true.B // RLAST

    // wiring for t_mem of type AXI4-Lite
    // -> {"aw":{"valid":1,"ready":-1,"bits":{"id":"awIdWidth","addr":"awAddrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"w":{"valid":1,"ready":-1,"bits":{"data":"wDataWidth","strb":"wStrbWidth","last":1}},"b":{"valid":-1,"ready":1,"bits":{"id":"-bIdWidth","resp":-2}},"ar":{"valid":1,"ready":-1,"bits":{"id":"arIdWidth","addr":"addrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"r":{"valid":-1,"ready":1,"bits":{"id":"-rIdWidth","data":"-dataWidth","resp":-2,"last":-1}}}
    // aw
    blackbox.io.t_mem_awvalid := t_mem0.aw.valid
    t_mem0.aw.ready := blackbox.io.t_mem_awready
    // aw
    // AWID
    blackbox.io.t_mem_awaddr := t_mem0.aw.bits.addr
    // AWLEN
    // AWSIZE
    // AWBURST
    // AWLOCK
    // AWCACHE
    blackbox.io.t_mem_awprot := t_mem0.aw.bits.prot
    // AWQOS
    // AWREGION
    // w
    blackbox.io.t_mem_wvalid := t_mem0.w.valid
    t_mem0.w.ready := blackbox.io.t_mem_wready
    // w
    blackbox.io.t_mem_wdata := t_mem0.w.bits.data
    blackbox.io.t_mem_wstrb := t_mem0.w.bits.strb
    // WLAST
    // b
    t_mem0.b.valid := blackbox.io.t_mem_bvalid
    blackbox.io.t_mem_bready := t_mem0.b.ready
    // b
    t_mem0.b.bits.id := 0.U // BID
    t_mem0.b.bits.resp := blackbox.io.t_mem_bresp
    // ar
    blackbox.io.t_mem_arvalid := t_mem0.ar.valid
    t_mem0.ar.ready := blackbox.io.t_mem_arready
    // ar
    // ARID
    blackbox.io.t_mem_araddr := t_mem0.ar.bits.addr
    // ARLEN
    // ARSIZE
    // ARBURST
    // ARLOCK
    // ARCACHE
    blackbox.io.t_mem_arprot := t_mem0.ar.bits.prot
    // ARQOS
    // ARREGION
    // r
    t_mem0.r.valid := blackbox.io.t_mem_rvalid
    blackbox.io.t_mem_rready := t_mem0.r.ready
    // r
    t_mem0.r.bits.id := 0.U // RID
    t_mem0.r.bits.data := blackbox.io.t_mem_rdata
    t_mem0.r.bits.resp := blackbox.io.t_mem_rresp
    t_mem0.r.bits.last := true.B // RLAST

    // wiring for irq of type interrupts
    // ["irq0","irq1"]

  }
  lazy val module = new LpioBaseImp
}


case class Pt_sidebandParams(
  base: BigInt,
  executable: Boolean = false,
  maxFifoBits: Int = 2,
  maxTransactions: Int = 1,
  axi4BufferParams: AXI4BufferParams = AXI4BufferParams(),
  tlBufferParams: TLBufferParams = TLBufferParams()
)


case class Pt_ctrlParams(
  base: BigInt,
  executable: Boolean = false,
  maxFifoBits: Int = 2,
  maxTransactions: Int = 1,
  axi4BufferParams: AXI4BufferParams = AXI4BufferParams(),
  tlBufferParams: TLBufferParams = TLBufferParams()
)


case class Pt_memParams(
  base: BigInt,
  executable: Boolean = false,
  maxFifoBits: Int = 2,
  maxTransactions: Int = 1,
  axi4BufferParams: AXI4BufferParams = AXI4BufferParams(),
  tlBufferParams: TLBufferParams = TLBufferParams()
)

case class PirqParams()


case class NpioTopParams(
  blackbox: pioParams
) {
  def setBurstBytes(x: Int): NpioTopParams = this.copy()
}

object NpioTopParams {
  def defaults(
    t_sideband_base: BigInt,
    t_ctrl_base: BigInt,
    t_mem_base: BigInt,
    cacheBlockBytes: Int
  ) = NpioTopParams(
    blackbox = pioParams(
      t_sidebandParams = Pt_sidebandParams(base = t_sideband_base),
      t_ctrlParams = Pt_ctrlParams(base = t_ctrl_base),
      t_memParams = Pt_memParams(base = t_mem_base),
      irqParams = PirqParams(),
      cacheBlockBytes = cacheBlockBytes
    )
  )
}


class NpioTopLogicalTreeNode(component: NpioTopBase) extends LogicalTreeNode(() => Some(component.imp.device)) {
  override def getOMComponents(resourceBindings: ResourceBindings, components: Seq[OMComponent]): Seq[OMComponent] = {
    val name = component.imp.device.describe(resourceBindings).name
    val omDevice = new scala.collection.mutable.LinkedHashMap[String, Any] with OMDevice {
      val memoryRegions = component.getOMMemoryRegions(resourceBindings)
      val interrupts = component.getOMInterrupts(resourceBindings)
      val _types: Seq[String] = Seq("OMpio", "OMDevice", "OMComponent", "OMCompoundType")
    }
    val userOM = component.userOM
    val values = userOM.productIterator
    if (values.nonEmpty) {
      val pairs = (userOM.getClass.getDeclaredFields.map { field =>
        assert(field.getName != "memoryRegions", "user Object Model must not define \"memoryRegions\"")
        assert(field.getName != "interrupts", "user Object Model must not define \"interrupts\"")
        assert(field.getName != "_types", "user Object Model must not define \"_types\"")

        field.getName -> values.next
      }).toSeq
      omDevice ++= pairs
    }
    omDevice("memoryRegions") = omDevice.memoryRegions
    omDevice("interrupts") = omDevice.interrupts
    omDevice("_types") = omDevice._types
    Seq(omDevice)
  }
}

class NpioTopBase(val c: NpioTopParams)(implicit p: Parameters)
 extends SimpleLazyModule
 with BindingScope
 with HasLogicalTreeNode {
  val imp = LazyModule(new Lpio(c.blackbox))

  ResourceBinding { Resource(imp.device, "exists").bind(ResourceString("yes")) }

  def userOM: Product with Serializable = Nil

  private def prettyPrintField(field: RegField, bitOffset: Int): String = {
    val nameOpt = field.desc.map(_.name)
    nameOpt.map(_ + " ").getOrElse("") + s"at offset $bitOffset"
  }
  private def padFields(fields: (Int, RegField)*) = {
    var previousOffset = 0
    var previousFieldOpt: Option[RegField] = None

    fields.sortBy({ case (offset, _) => offset }).flatMap { case (fieldOffset, field) =>
      val padWidth = fieldOffset - (previousOffset + previousFieldOpt.map(_.width).getOrElse(0))
      val prettyField = prettyPrintField(field, fieldOffset)
      require(
        padWidth >= 0,
        previousFieldOpt.map(previousField => {
          val prettyPrevField = prettyPrintField(previousField, previousOffset)
          s"register fields $prettyPrevField and $prettyField are overlapping"
        }).getOrElse(
          s"register field $prettyField has a negative offset"
        )
      )

      previousOffset = fieldOffset
      previousFieldOpt = Some(field)

      if (padWidth > 0) {
        Seq(RegField(padWidth), field)
      } else {
        Seq(field)
      }
    }
  }

  //SKETCH: make this a map from MemoryMap name to register map
  // SKETCH: note, we would be able to better avoid the "blowing up code size" issue if each
  // addressBlock call was broken out into a separate def and then just assembled here.
  // e.g.
  // def csrAddressBlock0 = RegFieldAddressBlock(...)
  // then
  // OMRegister.convertSeq(csrAddressBlock0 ++ csrAddressBlock1)
  def omRegisterMaps: Map[String, OMRegisterMap] = Map(
    // memoryMap: "CSR" with registers
    "CSR" -> OMRegister.convertSeq(
      // addressBlock: csrAddressBlock0
      RegFieldAddressBlock(
        AddressBlockInfo(
          "csrAddressBlock0",
          addressOffset = 0,
          range = 512,
          width = 32
        ),
        addAddressOffset = true,
        0 -> RegFieldGroup("ODATA", None, padFields(
          0 -> RegField(32, Bool(), RegFieldDesc("data", "")))),
        4 -> RegFieldGroup("OENABLE", Some("""determines whether the pin is an input or an output. If the data direction bit is a 1, then the pin is an input"""), padFields(
          0 -> RegField(32, Bool(), RegFieldDesc("data", "")))),
        8 -> RegFieldGroup("IDATA", Some("""read the port pins"""), padFields(
          0 -> RegField(32, Bool(), RegFieldDesc("data", ""))))
      ) ++
      // addressBlock: csrAddressBlock1
      RegFieldAddressBlock(
        AddressBlockInfo(
          "csrAddressBlock1",
          addressOffset = 512,
          range = 512,
          width = 32
        ),
        addAddressOffset = true,
        0 -> RegFieldGroup("FOO", None, padFields(
          0 -> RegField(5, Bool(), RegFieldDesc("foo", ""))))
      )
    ),
    // memoryMap: "JustMemoryNoRegisters",
    // memoryMap: "SIDEBAND_CSR" with registers
    "SIDEBAND_CSR" -> OMRegister.convertSeq(
      // addressBlock: SidebandCSRAddressBlock
      RegFieldAddressBlock(
        AddressBlockInfo(
          "SidebandCSRAddressBlock",
          addressOffset = 128,
          range = 512,
          width = 32
        ),
        addAddressOffset = true,
        0 -> RegFieldGroup("SomeRegister", None, padFields(
          0 -> RegField(16, Bool(), RegFieldDesc("data", "")),16 -> RegField(16, Bool(), RegFieldDesc("control", ""))))
      )
    )
  )

  //SKETCH: we should just put this code in the auto-generated class, right?
  //SKETCH: user can still override it if they want
  def getOMMemoryRegions(resourceBindings: ResourceBindings): Seq[OMMemoryRegion] = {
    val name = imp.device.describe(resourceBindings).name
    val diplomaticRegions = DiplomaticObjectModelAddressing.getOMMemoryRegions(name, resourceBindings, None)
    // associate register maps with memory regions in Object model
    val regMaps: Map[String, OMRegisterMap] = omRegisterMaps
    diplomaticRegions.map { case (memRegion) =>
      val regMapOpt = omRegisterMaps.lift(memRegion.description)
      memRegion.copy(registerMap = omRegisterMaps.lift(memRegion.description),
        addressBlocks = regMapOpt.map{_.addressBlocks}.getOrElse(Nil))
    }
  }

  def getOMInterrupts(resourceBindings: ResourceBindings): Seq[OMInterrupt] = {
    val name = imp.device.describe(resourceBindings).name
    DiplomaticObjectModelAddressing.describeGlobalInterrupts(name, resourceBindings)
  }

  def logicalTreeNode: LogicalTreeNode = new NpioTopLogicalTreeNode(this)

  val addrWidth: Int = c.blackbox.addrWidth
  val dataWidth: Int = c.blackbox.dataWidth
  val pioWidth: Int = c.blackbox.pioWidth
  val writeStrobeWidth: Int = c.blackbox.writeStrobeWidth
// no channel node

  val t_sidebandNode: AXI4SlaveNode = imp.t_sidebandNode

  def gett_sidebandNodeTLAdapter(): TLInwardNode = {(
    t_sidebandNode
      := AXI4Buffer(
        aw = c.blackbox.t_sidebandParams.axi4BufferParams.aw,
        ar = c.blackbox.t_sidebandParams.axi4BufferParams.ar,
        w = c.blackbox.t_sidebandParams.axi4BufferParams.w,
        r = c.blackbox.t_sidebandParams.axi4BufferParams.r,
        b = c.blackbox.t_sidebandParams.axi4BufferParams.b
      )
      := AXI4UserYanker(capMaxFlight = Some(c.blackbox.t_sidebandParams.maxTransactions))
      := TLToAXI4()
      := TLFragmenter(((dataWidth) / 8), c.blackbox.cacheBlockBytes, holdFirstDeny=true)
      := TLBuffer(
        a = c.blackbox.t_sidebandParams.tlBufferParams.a,
        b = c.blackbox.t_sidebandParams.tlBufferParams.b,
        c = c.blackbox.t_sidebandParams.tlBufferParams.c,
        d = c.blackbox.t_sidebandParams.tlBufferParams.d,
        e = c.blackbox.t_sidebandParams.tlBufferParams.e
      )
  )}


  val t_ctrlNode: AXI4SlaveNode = imp.t_ctrlNode

  def gett_ctrlNodeTLAdapter(): TLInwardNode = {(
    t_ctrlNode
      := AXI4Buffer(
        aw = c.blackbox.t_ctrlParams.axi4BufferParams.aw,
        ar = c.blackbox.t_ctrlParams.axi4BufferParams.ar,
        w = c.blackbox.t_ctrlParams.axi4BufferParams.w,
        r = c.blackbox.t_ctrlParams.axi4BufferParams.r,
        b = c.blackbox.t_ctrlParams.axi4BufferParams.b
      )
      := AXI4UserYanker(capMaxFlight = Some(c.blackbox.t_ctrlParams.maxTransactions))
      := TLToAXI4()
      := TLFragmenter(((dataWidth) / 8), c.blackbox.cacheBlockBytes, holdFirstDeny=true)
      := TLBuffer(
        a = c.blackbox.t_ctrlParams.tlBufferParams.a,
        b = c.blackbox.t_ctrlParams.tlBufferParams.b,
        c = c.blackbox.t_ctrlParams.tlBufferParams.c,
        d = c.blackbox.t_ctrlParams.tlBufferParams.d,
        e = c.blackbox.t_ctrlParams.tlBufferParams.e
      )
  )}


  val t_memNode: AXI4SlaveNode = imp.t_memNode

  def gett_memNodeTLAdapter(): TLInwardNode = {(
    t_memNode
      := AXI4Buffer(
        aw = c.blackbox.t_memParams.axi4BufferParams.aw,
        ar = c.blackbox.t_memParams.axi4BufferParams.ar,
        w = c.blackbox.t_memParams.axi4BufferParams.w,
        r = c.blackbox.t_memParams.axi4BufferParams.r,
        b = c.blackbox.t_memParams.axi4BufferParams.b
      )
      := AXI4UserYanker(capMaxFlight = Some(c.blackbox.t_memParams.maxTransactions))
      := TLToAXI4()
      := TLFragmenter(((dataWidth) / 8), c.blackbox.cacheBlockBytes, holdFirstDeny=true)
      := TLBuffer(
        a = c.blackbox.t_memParams.tlBufferParams.a,
        b = c.blackbox.t_memParams.tlBufferParams.b,
        c = c.blackbox.t_memParams.tlBufferParams.c,
        d = c.blackbox.t_memParams.tlBufferParams.d,
        e = c.blackbox.t_memParams.tlBufferParams.e
      )
  )}


  val irqNode: IntSourceNode = imp.irqNode
}

object NpioTopBase {
  def attach(c: NpioTopParams)(bap: BlockAttachParams): NpioTop = {
    implicit val p: Parameters = bap.p
    val pio_top = LazyModule(new NpioTop(c))
    // no channel attachment
    bap.pbus.coupleTo("axi") { pio_top.gett_sidebandNodeTLAdapter() := TLWidthWidget(bap.pbus) := _ }
    bap.pbus.coupleTo("axi") { pio_top.gett_ctrlNodeTLAdapter() := TLWidthWidget(bap.pbus) := _ }
    bap.pbus.coupleTo("axi") { pio_top.gett_memNodeTLAdapter() := TLWidthWidget(bap.pbus) := _ }
    LogicalModuleTree.add(bap.parentNode, pio_top.logicalTreeNode)
    bap.ibus := pio_top.irqNode
    pio_top
  }
}

class WithpioTopBase (
  t_sideband_base: BigInt,
  t_ctrl_base: BigInt,
  t_mem_base: BigInt
) extends Config((site, here, up) => {
  case BlockDescriptorKey =>
    BlockDescriptor(
      name = "pio",
      place = NpioTop.attach(NpioTopParams.defaults(
        t_sideband_base = t_sideband_base,
        t_ctrl_base = t_ctrl_base,
        t_mem_base = t_mem_base,
        cacheBlockBytes = site(CacheBlockBytes)
      ))
    ) +: up(BlockDescriptorKey, site)
})
