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
  val pioWidth: Int
) extends Bundle {
  val t_ctrl_awvalid = Input(Bool())
  val t_ctrl_awready = Output(Bool())
  val t_ctrl_awaddr = Input(UInt((addrWidth).W))
  val t_ctrl_awprot = Input(UInt((3).W))
  val t_ctrl_wvalid = Input(Bool())
  val t_ctrl_wready = Output(Bool())
  val t_ctrl_wdata = Input(UInt((dataWidth).W))
  val t_ctrl_wstrb = Input(UInt((dataWidth / 8).W))
  val t_ctrl_bvalid = Output(Bool())
  val t_ctrl_bready = Input(Bool())
  val t_ctrl_bresp = Output(UInt((2).W))
  val t_ctrl_arvalid = Input(Bool())
  val t_ctrl_arready = Output(Bool())
  val t_ctrl_araddr = Input(UInt((addrWidth).W))
  val t_ctrl_arprot = Input(UInt((3).W))
  val t_ctrl_rvalid = Output(Bool())
  val t_ctrl_rready = Input(Bool())
  val t_ctrl_rdata = Output(UInt((dataWidth).W))
  val t_ctrl_rresp = Output(UInt((2).W))
  val t_sideband_awvalid = Input(Bool())
  val t_sideband_awready = Output(Bool())
  val t_sideband_awaddr = Input(UInt((addrWidth).W))
  val t_sideband_awprot = Input(UInt((3).W))
  val t_sideband_wvalid = Input(Bool())
  val t_sideband_wready = Output(Bool())
  val t_sideband_wdata = Input(UInt((dataWidth).W))
  val t_sideband_wstrb = Input(UInt((dataWidth/8).W))
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
  val t_mem_wstrb = Input(UInt((dataWidth/8).W))
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
  val pioWidth: Int
) extends BlackBox(Map(
  "addrWidth" -> core.IntParam(addrWidth),
  "dataWidth" -> core.IntParam(dataWidth),
  "pioWidth" -> core.IntParam(pioWidth)
)) with HasBlackBoxResource {
  val io = IO(new pioBlackBoxIO(
    addrWidth,
    dataWidth,
    pioWidth
  ))
// setResource("top.v")
}

case class pioParams(
  addrWidth: Int = 12,
  dataWidth: Int = 32,
  pioWidth: Int = 32,
  ctrlParams: PctrlParams,
  sidebandParams: PsidebandParams,
  memParams: PmemParams,
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

  val sidebandNode = AXI4SlaveNode(Seq(
    AXI4SlavePortParameters(
      slaves = Seq(
        AXI4SlaveParameters(
          address       = List(AddressSet(c.sidebandParams.base, ((1L << addrWidth) - 1))),
          executable    = c.sidebandParams.executable,
          supportsWrite = TransferSizes(1, ((dataWidth) * 1 / 8)),
          supportsRead  = TransferSizes(1, ((dataWidth) * 1 / 8)),
          interleavedId = Some(0),
          // SKETCH: Need to put the correct memory map name here to distinguish
          resources     = device.reg("SIDEBAND_CSR")
        )
      ),
      beatBytes = (dataWidth) / 8
    )
  ))

  val ctrlNode = AXI4SlaveNode(Seq(
    AXI4SlavePortParameters(
      slaves = Seq(
        AXI4SlaveParameters(
          address       = List(AddressSet(c.ctrlParams.base, ((1L << addrWidth) - 1))),
          executable    = c.ctrlParams.executable,
          supportsWrite = TransferSizes(1, (dataWidth / 8)),
          supportsRead  = TransferSizes(1, (dataWidth / 8)),
          interleavedId = Some(0),
          resources     = device.reg
        )
      ),
      beatBytes = dataWidth / 8
    )
  ))

  val irqNode = IntSourceNode(IntSourcePortSimple(
    num = 2,
    resources = device.int
  ))

  val memNode = AXI4SlaveNode(Seq(
    AXI4SlavePortParameters(
      slaves = Seq(
        AXI4SlaveParameters(
          address       = List(AddressSet(c.memParams.base, ((1L << addrWidth) - 1))),
          executable    = c.memParams.executable,
          supportsWrite = TransferSizes(1, ((dataWidth) * 1 / 8)),
          supportsRead  = TransferSizes(1, ((dataWidth) * 1 / 8)),
          interleavedId = Some(0),
          // SKETCH: Need to put the correct memory map name here to distinguish
          resources     = device.reg("JustMemoryNoRegisters")
        )
      ),
      beatBytes = (dataWidth) / 8
    )
  ))


  val ioBridgeSource = BundleBridgeSource(() => new pioBlackBoxIO(
    c.addrWidth,
    c.dataWidth,
    c.pioWidth
  ))

  class LpioBaseImp extends LazyRawModuleImp(this) {
    val blackbox = Module(new pio(
      c.addrWidth,
      c.dataWidth,
      c.pioWidth
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
    val sideband0 = sidebandNode.in(0)._1
    val ctrl0 = ctrlNode.in(0)._1
    val mem0 = memNode.in(0)._1
    val irq0 = irqNode.out(0)._1
    // interface wiring
    // wiring for t_sideband of type AXI4-Lite
    // -> {"aw":{"valid":1,"ready":-1,"bits":{"id":"awIdWidth","addr":"awAddrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"w":{"valid":1,"ready":-1,"bits":{"data":"wDataWidth","strb":"wStrbWidth","last":1}},"b":{"valid":-1,"ready":1,"bits":{"id":"-bIdWidth","resp":-2}},"ar":{"valid":1,"ready":-1,"bits":{"id":"arIdWidth","addr":"addrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"r":{"valid":-1,"ready":1,"bits":{"id":"-rIdWidth","data":"-dataWidth","resp":-2,"last":-1}}}
    // aw
    blackbox.io.t_sideband_awvalid := sideband0.aw.valid
    sideband0.aw.ready := blackbox.io.t_sideband_awready
    // aw
    // AWID
    blackbox.io.t_sideband_awaddr := sideband0.aw.bits.addr
    // AWLEN
    // AWSIZE
    // AWBURST
    // AWLOCK
    // AWCACHE
    blackbox.io.t_sideband_awprot := sideband0.aw.bits.prot
    // AWQOS
    // AWREGION
    // w
    blackbox.io.t_sideband_wvalid := sideband0.w.valid
    sideband0.w.ready := blackbox.io.t_sideband_wready
    // w
    blackbox.io.t_sideband_wdata := sideband0.w.bits.data
    blackbox.io.t_sideband_wstrb := sideband0.w.bits.strb
    // WLAST
    // b
    sideband0.b.valid := blackbox.io.t_sideband_bvalid
    blackbox.io.t_sideband_bready := sideband0.b.ready
    // b
    sideband0.b.bits.id := 0.U // BID
    sideband0.b.bits.resp := blackbox.io.t_sideband_bresp
    // ar
    blackbox.io.t_sideband_arvalid := sideband0.ar.valid
    sideband0.ar.ready := blackbox.io.t_sideband_arready
    // ar
    // ARID
    blackbox.io.t_sideband_araddr := sideband0.ar.bits.addr
    // ARLEN
    // ARSIZE
    // ARBURST
    // ARLOCK
    // ARCACHE
    blackbox.io.t_sideband_arprot := sideband0.ar.bits.prot
    // ARQOS
    // ARREGION
    // r
    sideband0.r.valid := blackbox.io.t_sideband_rvalid
    blackbox.io.t_sideband_rready := sideband0.r.ready
    // r
    sideband0.r.bits.id := 0.U // RID
    sideband0.r.bits.data := blackbox.io.t_sideband_rdata
    sideband0.r.bits.resp := blackbox.io.t_sideband_rresp
    sideband0.r.bits.last := true.B // RLAST    


    // wiring for ctrl of type AXI4-Lite
    // -> {"aw":{"valid":1,"ready":-1,"bits":{"id":"awIdWidth","addr":"awAddrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4}},"w":{"valid":1,"ready":-1,"bits":{"data":"wDataWidth","strb":"wStrbWidth","last":1}},"b":{"valid":-1,"ready":1,"bits":{"id":"-bIdWidth","resp":-2}},"ar":{"valid":1,"ready":-1,"bits":{"id":"arIdWidth","addr":"addrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4}},"r":{"valid":-1,"ready":1,"bits":{"id":"-rIdWidth","data":"-dataWidth","resp":-2,"last":-1}}}
    // aw
    blackbox.io.t_ctrl_awvalid := ctrl0.aw.valid
    ctrl0.aw.ready := blackbox.io.t_ctrl_awready
    // aw
    // AWID
    blackbox.io.t_ctrl_awaddr := ctrl0.aw.bits.addr
    // AWLEN
    // AWSIZE
    // AWBURST
    // AWLOCK
    // AWCACHE
    blackbox.io.t_ctrl_awprot := ctrl0.aw.bits.prot
    // AWQOS
    // w
    blackbox.io.t_ctrl_wvalid := ctrl0.w.valid
    ctrl0.w.ready := blackbox.io.t_ctrl_wready
    // w
    blackbox.io.t_ctrl_wdata := ctrl0.w.bits.data
    blackbox.io.t_ctrl_wstrb := ctrl0.w.bits.strb
    // WLAST
    // b
    ctrl0.b.valid := blackbox.io.t_ctrl_bvalid
    blackbox.io.t_ctrl_bready := ctrl0.b.ready
    // b
    ctrl0.b.bits.id := 0.U // BID
    ctrl0.b.bits.resp := blackbox.io.t_ctrl_bresp
    // ar
    blackbox.io.t_ctrl_arvalid := ctrl0.ar.valid
    ctrl0.ar.ready := blackbox.io.t_ctrl_arready
    // ar
    // ARID
    blackbox.io.t_ctrl_araddr := ctrl0.ar.bits.addr
    // ARLEN
    // ARSIZE
    // ARBURST
    // ARLOCK
    // ARCACHE
    blackbox.io.t_ctrl_arprot := ctrl0.ar.bits.prot
    // ARQOS
    // r
    ctrl0.r.valid := blackbox.io.t_ctrl_rvalid
    blackbox.io.t_ctrl_rready := ctrl0.r.ready
    // r
    ctrl0.r.bits.id := 0.U // RID
    ctrl0.r.bits.data := blackbox.io.t_ctrl_rdata
    ctrl0.r.bits.resp := blackbox.io.t_ctrl_rresp
    ctrl0.r.bits.last := true.B // RLAST

    // wiring for t_mem of type AXI4-Lite
    // -> {"aw":{"valid":1,"ready":-1,"bits":{"id":"awIdWidth","addr":"awAddrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"w":{"valid":1,"ready":-1,"bits":{"data":"wDataWidth","strb":"wStrbWidth","last":1}},"b":{"valid":-1,"ready":1,"bits":{"id":"-bIdWidth","resp":-2}},"ar":{"valid":1,"ready":-1,"bits":{"id":"arIdWidth","addr":"addrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4,"region":4}},"r":{"valid":-1,"ready":1,"bits":{"id":"-rIdWidth","data":"-dataWidth","resp":-2,"last":-1}}}
    // aw
    blackbox.io.t_mem_awvalid := mem0.aw.valid
    mem0.aw.ready := blackbox.io.t_mem_awready
    // aw
    // AWID
    blackbox.io.t_mem_awaddr := mem0.aw.bits.addr
    // AWLEN
    // AWSIZE
    // AWBURST
    // AWLOCK
    // AWCACHE
    blackbox.io.t_mem_awprot := mem0.aw.bits.prot
    // AWQOS
    // AWREGION
    // w
    blackbox.io.t_mem_wvalid := mem0.w.valid
    mem0.w.ready := blackbox.io.t_mem_wready
    // w
    blackbox.io.t_mem_wdata := mem0.w.bits.data
    blackbox.io.t_mem_wstrb := mem0.w.bits.strb
    // WLAST
    // b
    mem0.b.valid := blackbox.io.t_mem_bvalid
    blackbox.io.t_mem_bready := mem0.b.ready
    // b
    mem0.b.bits.id := 0.U // BID
    mem0.b.bits.resp := blackbox.io.t_mem_bresp
    // ar
    blackbox.io.t_mem_arvalid := mem0.ar.valid
    mem0.ar.ready := blackbox.io.t_mem_arready
    // ar
    // ARID
    blackbox.io.t_mem_araddr := mem0.ar.bits.addr
    // ARLEN
    // ARSIZE
    // ARBURST
    // ARLOCK
    // ARCACHE
    blackbox.io.t_mem_arprot := mem0.ar.bits.prot
    // ARQOS
    // ARREGION
    // r
    mem0.r.valid := blackbox.io.t_mem_rvalid
    blackbox.io.t_mem_rready := mem0.r.ready
    // r
    mem0.r.bits.id := 0.U // RID
    mem0.r.bits.data := blackbox.io.t_mem_rdata
    mem0.r.bits.resp := blackbox.io.t_mem_rresp
    mem0.r.bits.last := true.B // RLAST

    // wiring for irq of type interrupts
    // ["irq0","irq1"]

  }
  lazy val module = new LpioBaseImp
}

case class PsidebandParams(
  base: BigInt,
  executable: Boolean = false,
  maxFifoBits: Int = 2,
  maxTransactions: Int = 1,
  axi4BufferParams: AXI4BufferParams = AXI4BufferParams(),
  tlBufferParams: TLBufferParams = TLBufferParams()
)

case class PctrlParams(
  base: BigInt,
  executable: Boolean = false,
  maxFifoBits: Int = 2,
  maxTransactions: Int = 1,
  axi4BufferParams: AXI4BufferParams = AXI4BufferParams(),
  tlBufferParams: TLBufferParams = TLBufferParams()
)



case class PmemParams(
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
    sideband_base: BigInt,
    ctrl_base: BigInt,
    mem_base: BigInt,
    cacheBlockBytes: Int
  ) = NpioTopParams(
    blackbox = pioParams(
      sidebandParams = PsidebandParams(base = sideband_base),
      ctrlParams = PctrlParams(base = ctrl_base),
      memParams = PmemParams(base = mem_base),
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

  private def padFields(fields: (Int, RegField)*) = {
    var previousOffset = 0
    var previousField: Option[RegField] = None

    fields.flatMap { case (fieldOffset, field) =>
      val padWidth = fieldOffset - previousOffset
      require(padWidth >= 0,
        if (previousField.isDefined) {
          s"register fields at $previousOffset and $fieldOffset are overlapping"
        } else {
          s"register field $field has a negative offset"
        })

      previousOffset = fieldOffset
      previousField = Some(field)

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
    "CSR" ->
      // SKETCH: use new method convertSeq instead of plain convert because AddressBlock returns a Seq
      OMRegister.convertSeq(
        // SKETCH: these come from the JSON addressBlock
        RegFieldAddressBlock(AddressBlockInfo("csrAddressBlock0", addressOffset = 0, range=512, width=32),
        // SKETCH: set this to true so that you don't have to add the offsets in the 0 -> lines below
        addAddressOffset = true,
      0 -> RegFieldGroup("ODATA", None, padFields(
        0 -> RegField(32, Bool(), RegFieldDesc("data", "")))),
      4 -> RegFieldGroup("OENABLE", Some("""determines whether the pin is an input or an output. If the data direction bit is a 1, then the pin is an input"""), padFields(
        0 -> RegField(32, Bool(), RegFieldDesc("data", "")))),
      8 -> RegFieldGroup("IDATA", Some("""read the port pins"""), padFields(
        0 -> RegField(32, Bool(), RegFieldDesc("data", ""))))
    ) ++ // SKETCH: note appending these within one single OMRegister.convert(...) call
    RegFieldAddressBlock(AddressBlockInfo(name = "csrAddressBlock1",
      addressOffset = 512, range = 512, width=32),
      addAddressOffset = true,
      0 -> RegFieldGroup("FOO", None, padFields(
        0 -> RegField(5, Bool(), RegFieldDesc("foo", ""))))
    )),
    //OMRegister.convert
    // SKETCH: makes no sense to include empty register map
    // OMRegister.convert(
    //   ),
    //SKETCH: note the second memory region mapping
    "SIDEBAND_CSR" ->  OMRegister.convertSeq(
      //SKETCH: adding the address block info
        RegFieldAddressBlock(AddressBlockInfo(name = "SidebandCSRAddressBlock",
          addressOffset = 128 , range = 512 , width= 32),
          addAddressOffset = true,
      0 -> RegFieldGroup("SomeRegister", None, padFields(
        0 -> RegField(16, Bool(), RegFieldDesc("data", "")),16 -> RegField(16, Bool(), RegFieldDesc("control", ""))
      )))
    )
  )
  

  //SKETCH: we should just put this code in the auto-generated class, right?
  //SKETCH: user can still override it if they want
  def getOMMemoryRegions(resourceBindings: ResourceBindings): Seq[OMMemoryRegion] = {
    val name = imp.device.describe(resourceBindings).name
    val diplomaticRegions = DiplomaticObjectModelAddressing.getOMMemoryRegions(name, resourceBindings, None)
      // associate register maps with memory regions in Object model
    diplomaticRegions.map { case (memRegion) =>
      val regMaps: Map[String, OMRegisterMap] = omRegisterMaps
      memRegion.copy(registerMap = omRegisterMaps.lift(memRegion.name))
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
// no channel node

val sidebandNode: AXI4SlaveNode = imp.sidebandNode  
val ctrlNode: AXI4SlaveNode = imp.ctrlNode

    def getsidebandNodeTLAdapter(): TLInwardNode = {(
    sidebandNode
      := AXI4Buffer(
        aw = c.blackbox.sidebandParams.axi4BufferParams.aw,
        ar = c.blackbox.sidebandParams.axi4BufferParams.ar,
        w = c.blackbox.sidebandParams.axi4BufferParams.w,
        r = c.blackbox.sidebandParams.axi4BufferParams.r,
        b = c.blackbox.sidebandParams.axi4BufferParams.b
      )
      := AXI4UserYanker(capMaxFlight = Some(c.blackbox.sidebandParams.maxTransactions))
      := TLToAXI4()
      := TLFragmenter(((dataWidth) / 8), c.blackbox.cacheBlockBytes, holdFirstDeny=true)
      := TLBuffer(
        a = c.blackbox.sidebandParams.tlBufferParams.a,
        b = c.blackbox.sidebandParams.tlBufferParams.b,
        c = c.blackbox.sidebandParams.tlBufferParams.c,
        d = c.blackbox.sidebandParams.tlBufferParams.d,
        e = c.blackbox.sidebandParams.tlBufferParams.e
      )
  )}
  
  def getctrlNodeTLAdapter(): TLInwardNode = {(
    ctrlNode
      := AXI4Buffer(
        aw = c.blackbox.ctrlParams.axi4BufferParams.aw,
        ar = c.blackbox.ctrlParams.axi4BufferParams.ar,
        w = c.blackbox.ctrlParams.axi4BufferParams.w,
        r = c.blackbox.ctrlParams.axi4BufferParams.r,
        b = c.blackbox.ctrlParams.axi4BufferParams.b
      )
      := AXI4UserYanker(capMaxFlight = Some(c.blackbox.ctrlParams.maxTransactions))
      := TLToAXI4()
      := TLFragmenter((dataWidth / 8), c.blackbox.cacheBlockBytes, holdFirstDeny=true)
      := TLBuffer(
        a = c.blackbox.ctrlParams.tlBufferParams.a,
        b = c.blackbox.ctrlParams.tlBufferParams.b,
        c = c.blackbox.ctrlParams.tlBufferParams.c,
        d = c.blackbox.ctrlParams.tlBufferParams.d,
        e = c.blackbox.ctrlParams.tlBufferParams.e
      )
  )}

  val memNode: AXI4SlaveNode = imp.memNode

  def getmemNodeTLAdapter(): TLInwardNode = {(
    memNode
      := AXI4Buffer(
        aw = c.blackbox.memParams.axi4BufferParams.aw,
        ar = c.blackbox.memParams.axi4BufferParams.ar,
        w = c.blackbox.memParams.axi4BufferParams.w,
        r = c.blackbox.memParams.axi4BufferParams.r,
        b = c.blackbox.memParams.axi4BufferParams.b
      )
      := AXI4UserYanker(capMaxFlight = Some(c.blackbox.memParams.maxTransactions))
      := TLToAXI4()
      := TLFragmenter(((dataWidth) / 8), c.blackbox.cacheBlockBytes, holdFirstDeny=true)
      := TLBuffer(
        a = c.blackbox.memParams.tlBufferParams.a,
        b = c.blackbox.memParams.tlBufferParams.b,
        c = c.blackbox.memParams.tlBufferParams.c,
        d = c.blackbox.memParams.tlBufferParams.d,
        e = c.blackbox.memParams.tlBufferParams.e
      )
  )}


  val irqNode: IntSourceNode = imp.irqNode
}

object NpioTopBase {
  def attach(c: NpioTopParams)(bap: BlockAttachParams): NpioTop = {
    implicit val p: Parameters = bap.p
    val pio_top = LazyModule(new NpioTop(c))
    // no channel attachment
    bap.pbus.coupleTo("axi") { pio_top.getsidebandNodeTLAdapter() := TLWidthWidget(bap.pbus) := _ }
    bap.pbus.coupleTo("axi") { pio_top.getctrlNodeTLAdapter() := TLWidthWidget(bap.pbus) := _ }
    bap.pbus.coupleTo("axi") { pio_top.getmemNodeTLAdapter() := TLWidthWidget(bap.pbus) := _ }
    bap.ibus := pio_top.irqNode
LogicalModuleTree.add(bap.parentNode, pio_top.logicalTreeNode)
    pio_top
  }
}

class WithpioTopBase (
  sideband_base: BigInt,
  ctrl_base: BigInt,
  mem_base: BigInt
) extends Config((site, here, up) => {
  case BlockDescriptorKey =>
    BlockDescriptor(
      name = "pio",
      place = NpioTop.attach(NpioTopParams.defaults(
        sideband_base = sideband_base,
        ctrl_base = ctrl_base,
        mem_base = mem_base,
        cacheBlockBytes = site(CacheBlockBytes)
      ))
    ) +: up(BlockDescriptorKey, site)
})
