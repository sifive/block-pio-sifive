// USER editable file


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

import sifive.vip.loopback._

class NpioTopIO(
  val pioWidth: Int
) extends Bundle {
  val odata = Output(UInt(pioWidth.W))
  val oenable = Output(UInt(pioWidth.W))
  val idata = Input(UInt(pioWidth.W))
}

class Lpio(c: pioParams)(implicit p: Parameters) extends LpioBase(c)(p)
{

// User code here
  override def extraResources(resources: ResourceBindings) =
      Map("pio-width" -> Seq(ResourceInt(pioWidth)))

}

case class OMPIO(width: Int)

class NpioTop(c: NpioTopParams)(implicit p: Parameters) extends NpioTopBase(c)(p)
{
  // add in custom fields to the Object Model entry for this block
  override val userOM: OMPIO = OMPIO(c.blackbox.pioWidth)

  // route the ports of the black box to this sink
  val ioBridgeSink = BundleBridgeSink[pioBlackBoxIO]()
  ioBridgeSink := imp.ioBridgeSource

  // associate register maps with memory regions in Object model
  override def getOMMemoryRegions(resourceBindings: ResourceBindings) = {
    super.getOMMemoryRegions(resourceBindings).zip(omRegisterMaps).map { case (memRegion, regmap) =>
      memRegion.copy(registerMap = Some(regmap))
    }
  }

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
}

object NpioTop {
  def attach(c: NpioTopParams)(bap: BlockAttachParams): NpioTop = {
    val pio = NpioTopBase.attach(c)(bap)

    implicit val p: Parameters = bap.p

    // instantiate and connect the loopback vip in the testharness
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

    pio
  }
}

class WithpioTop extends Config(
  new WithpioTopBase(
    ctrl_base = 0x60000L
  )

    // User code here
)
