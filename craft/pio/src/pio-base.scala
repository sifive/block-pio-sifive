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
  val dataWidth: Int
) extends Bundle {
  val in_wdata = Input(UInt((dataWidth).W))
  val in_wenable = Input(UInt((dataWidth).W))
  val in_rdata = Output(UInt((dataWidth).W))
  val out_wdata = Output(UInt((dataWidth).W))
  val out_wenable = Output(UInt((dataWidth).W))
  val out_rdata = Input(UInt((dataWidth).W))
  val irq0 = Output(Bool())
  val irq1 = Output(Bool())
}

class pio(
  val dataWidth: Int
) extends BlackBox(Map(
  "dataWidth" -> core.IntParam(dataWidth)
)) with HasBlackBoxResource {
  val io = IO(new pioBlackBoxIO(
    dataWidth
  ))
// setResource("top.v")
}

case class pioParams(
  dataWidth: Int = 32,
  irqParams: PirqParams,
  cacheBlockBytes: Int
)

// busType: interrupts, mode: master

class LpioBase(c: pioParams)(implicit p: Parameters) extends LazyModule {

  val dataWidth = c.dataWidth

  val irqNode = IntSourceNode(IntSourcePortSimple(num = 2))

  val ioBridgeSource = BundleBridgeSource(() => new pioBlackBoxIO(
    c.dataWidth
  ))

  class LpioBaseImp extends LazyRawModuleImp(this) {
    val blackbox = Module(new pio(
      c.dataWidth
    ))
    // interface wiring 2

    // port wiring
    blackbox.io.in_wdata := ioBridgeSource.bundle.in_wdata
    blackbox.io.in_wenable := ioBridgeSource.bundle.in_wenable
    ioBridgeSource.bundle.in_rdata := blackbox.io.in_rdata
    ioBridgeSource.bundle.out_wdata := blackbox.io.out_wdata
    ioBridgeSource.bundle.out_wenable := blackbox.io.out_wenable
    blackbox.io.out_rdata := ioBridgeSource.bundle.out_rdata
    ioBridgeSource.bundle.irq0 := blackbox.io.irq0
    ioBridgeSource.bundle.irq1 := blackbox.io.irq1
    // interface alias
    //val irq0 = intNode.out(0)._1
    // interface wiring
    // wiring for irq of type interrupts
    // ["irq0","irq1"]

  }
  lazy val module = new LpioBaseImp
}


case class PirqParams()


case class NpioTopParams(
  blackbox: pioParams,
  ctrl_base: Long
) {
  def setBurstBytes(x: Int): NpioTopParams = this.copy()
}

object NpioTopParams {
  def defaults(
    cacheBlockBytes: Int,
    ctrl_base: Long
  ) = NpioTopParams(
    blackbox = pioParams(
      irqParams = PirqParams(),
      cacheBlockBytes = cacheBlockBytes
    ),
    ctrl_base = ctrl_base
  )
}

class NpioTopBase(c: NpioTopParams)(implicit p: Parameters) extends SimpleLazyModule {

  def extraResources(resources: ResourceBindings) = Map[String, Seq[ResourceValue]]()

  val device = new SimpleDevice("Pio", Seq("sifive,pio")) {
    override def describe(resources: ResourceBindings): Description = {
      val Description(name, mapping) = super.describe(resources)
      Description(name, mapping ++ extraResources(resources))
    }
  }

  ResourceBinding { Resource(device, "exists").bind(ResourceString("yes")) }

  val imp = LazyModule(new Lpio(c.blackbox))
  val dataWidth: Int = c.blackbox.dataWidth
// no channel node

  val irqNode: IntSourceNode = imp.irqNode
}

object NpioTopBase {
  def attach(c: NpioTopParams)(bap: BlockAttachParams): NpioTop = {
    implicit val p: Parameters = bap.p
    val pio_top = LazyModule(new NpioTop(c))
    // no channel attachment
    bap.ibus := pio_top.irqNode
    pio_top
  }
}

class WithpioTopBase (
  ctrl_base: Long
) extends Config((site, here, up) => {
  case BlockDescriptorKey =>
    BlockDescriptor(
      name = "pio",
      place = NpioTop.attach(NpioTopParams.defaults(
        cacheBlockBytes = site(CacheBlockBytes),
        ctrl_base = ctrl_base
      ))
    ) +: up(BlockDescriptorKey, site)
})
