// Generated Code
// Please DO NOT EDIT


package sifive.blocks.pio

import chisel3._
// import chisel3.{withClockAndReset, _}
import chisel3.util._
import chisel3.experimental._

import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
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
  val odata = Output(UInt((pioWidth).W))
  val oenable = Output(UInt((pioWidth).W))
  val idata = Input(UInt((pioWidth).W))
  val irq0 = Output(Bool())
  val irq1 = Output(Bool())
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
  irqParams: PirqParams,
  cacheBlockBytes: Int
)

class LpioBase(c: pioParams)(implicit p: Parameters) extends LazyModule {
  val device = new SimpleDevice("pio", Seq("sifive,pio-v0"))

  val addrWidth = c.addrWidth
  val dataWidth = c.dataWidth
  val pioWidth = c.pioWidth

  val ctrlNode = AXI4SlaveNode(Seq(
    AXI4SlavePortParameters(
      slaves = Seq(
        AXI4SlaveParameters(
          address       = List(AddressSet(c.ctrlParams.base, ((1L << addrWidth) - 1))),
          executable    = c.ctrlParams.executable,
          supportsWrite = TransferSizes(1, (dataWidth / 8)),
          supportsRead  = TransferSizes(1, (dataWidth / 8)),
          interleavedId = Some(0)
        )
      ),
      beatBytes = dataWidth / 8
    )
  ))

  val irqNode = IntSourceNode(IntSourcePortSimple(num = 2))

// busType: AXI4Lite, mode: slave
// busType: interrupts, mode: master

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
    ioBridgeSource.bundle.odata := blackbox.io.odata
    ioBridgeSource.bundle.oenable := blackbox.io.oenable
    blackbox.io.idata := ioBridgeSource.bundle.idata
    ioBridgeSource.bundle.irq0 := blackbox.io.irq0
    ioBridgeSource.bundle.irq1 := blackbox.io.irq1
    blackbox.io.clk := ioBridgeSource.bundle.clk
    blackbox.io.reset_n := ioBridgeSource.bundle.reset_n
    // interface alias
    val ctrl0 = ctrlNode.in(0)._1
    val irq0 = irqNode.out(0)._1
    // interface wiring
    // wiring for ctrl of type AXI4Lite
    // -> {"aw":{"valid":1,"ready":-1,"bits":{"id":"awIdWidth","addr":"awAddrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4}},"w":{"valid":1,"ready":-1,"bits":{"data":"wDataWidth","strb":"wStrbWidth","last":1}},"b":{"valid":-1,"ready":1,"bits":{"id":"-bIdWidth","resp":-2}},"ar":{"valid":1,"ready":-1,"bits":{"id":"arIdWidth","addr":"addrWidth","len":8,"size":3,"burst":2,"lock":1,"cache":4,"prot":3,"qos":4}},"r":{"valid":-1,"ready":1,"bits":{"id":"-rIdWidth","data":"-dataWidth","resp":-2,"last":-1}}}// aw
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

    // wiring for irq of type interrupts
    // ["irq0","irq1"]
  }
  lazy val module = new LpioBaseImp
}


case class PctrlParams(
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
    ctrl_base: BigInt,
    cacheBlockBytes: Int
  ) = NpioTopParams(
    blackbox = pioParams(
      ctrlParams = PctrlParams(base = ctrl_base),
      irqParams = PirqParams(),
      cacheBlockBytes = cacheBlockBytes
    )
  )
}

class NpioTopBase(c: NpioTopParams)(implicit p: Parameters) extends SimpleLazyModule {
  val imp = LazyModule(new Lpio(c.blackbox))
  val addrWidth: Int = c.blackbox.addrWidth
  val dataWidth: Int = c.blackbox.dataWidth
  val pioWidth: Int = c.blackbox.pioWidth
// no channel node

  val ctrlNode: AXI4SlaveNode = imp.ctrlNode

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


  val irqNode: IntSourceNode = imp.irqNode
}

object NpioTopBase {
  def attach(c: NpioTopParams)(bap: BlockAttachParams): NpioTop = {
    implicit val p: Parameters = bap.p
    val pio_top = LazyModule(new NpioTop(c))
    // no channel attachment
    bap.pbus.coupleTo("axi") { pio_top.getctrlNodeTLAdapter() := TLWidthWidget(bap.pbus) := _ }
    bap.ibus := pio_top.irqNode
    pio_top
  }
}

class WithpioTopBase (
  ctrl_base: BigInt
) extends Config((site, here, up) => {
  case BlockDescriptorKey =>
    BlockDescriptor(
      name = "pio",
      place = NpioTop.attach(NpioTopParams.defaults(
        ctrl_base = ctrl_base,
        cacheBlockBytes = site(CacheBlockBytes)
      ))
    ) +: up(BlockDescriptorKey, site)
})
