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

import freechips.rocketchip.diplomaticobjectmodel.logicaltree.{LogicalTreeNode}
import freechips.rocketchip.diplomaticobjectmodel.model._
import freechips.rocketchip.diplomaticobjectmodel.{DiplomaticObjectModelAddressing, HasLogicalTreeNode}

import sifive.skeleton._
import sifive.blocks.util.{NonBlockingEnqueue, NonBlockingDequeue}

import sifive.vip.loopback._
import sifive.blocks.pio.CSR.csrAddressBlock._

class NpioTopIO(
  val dataWidth: Int
) extends Bundle {
  val odata = Output(UInt(dataWidth.W))
  val oenable = Output(UInt(dataWidth.W))
  val idata = Input(UInt(dataWidth.W))
}

class Lpio(c: pioParams)(implicit p: Parameters) extends LpioBase(c)(p)
{

// User code here

}


case class OMPIO(
  blackbox: pioParams,
  memoryRegions: Seq[OMMemoryRegion],
  interrupts: Seq[OMInterrupt],
  _types: Seq[String] = Seq("OMGPIO", "OMDevice", "OMComponent", "OMCompoundType")
) extends OMDevice

class NpioTopLogicalTreeNode(device: SimpleDevice, pio: NpioTop) extends LogicalTreeNode(() => Some(device)) {
  override def getOMComponents(resourceBindings: ResourceBindings, components: Seq[OMComponent]): Seq[OMComponent] = {
    DiplomaticObjectModelAddressing.getOMComponentHelper(
      resourceBindings, (resources) => {
      Seq()
      })
  }
}

class NpioTop(val c: NpioTopParams)(implicit p: Parameters) extends NpioTopBase(c)(p)
  with HasLogicalTreeNode
{

  // route the ports of the black box to this sink
  val ioBridgeSink = BundleBridgeSink[pioBlackBoxIO]()
  ioBridgeSink := imp.ioBridgeSource

  // create a new ports for odata, oenable, and idata
  val ioBridgeSource = BundleBridgeSource(() => new NpioTopIO(c.blackbox.dataWidth))
  val regmap = LazyModule(new csrAddressBlockTLRegMap(p(CacheBlockBytes), c.ctrl_base))

  val registerIO = BundleBridgeSink[csrAddressBlockAddressBlockBundle]()
  registerIO := regmap.ioNode

  def logicalTreeNode: LogicalTreeNode = new NpioTopLogicalTreeNode(device, this)

  // logic to connect ioBridgeSink/Source nodes
  override lazy val module = new LazyModuleImp(this) {
    // connect ioBridge source and sink
    ioBridgeSource.bundle.odata   := ioBridgeSink.bundle.out_wdata
    ioBridgeSource.bundle.oenable := ioBridgeSink.bundle.out_wenable
    ioBridgeSink.bundle.out_rdata := ioBridgeSource.bundle.idata

    ioBridgeSink.bundle.in_wdata   := registerIO.bundle.ODATA.data
    ioBridgeSink.bundle.in_wenable := registerIO.bundle.OENABLE.data
    registerIO.bundle.IDATA.data := ioBridgeSink.bundle.in_rdata
  }
}

object NpioTop {
  def attach(c: NpioTopParams)(bap: BlockAttachParams): NpioTop = {
    val pio = NpioTopBase.attach(c)(bap)

    implicit val p: Parameters = bap.p

    // instantiate and connect the loopback vip in the testharness
    bap.testHarness {
      // instantiate the loopback vip
      val loopbackP = NloopbackTopParams(
        blackbox = loopbackParams(
          dataWidth = c.blackbox.dataWidth,
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
        loopbackNode.bundle.wdata   := pioNode.bundle.odata
        loopbackNode.bundle.wenable := pioNode.bundle.oenable
        pioNode.bundle.idata        := loopbackNode.bundle.rdata
      }
    }

    bap.pbus.coupleTo("pio") { pio.regmap.controlXing(NoCrossing) := TLWidthWidget(bap.pbus) := _ }

    pio
  }
}

class WithpioTop extends Config(
  new WithpioTopBase(
    ctrl_base = 0x60000L
  )

    // User code here
)
