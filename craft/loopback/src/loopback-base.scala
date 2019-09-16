// Generated Code
// Please DO NOT EDIT


package sifive.vip.loopback

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



class loopbackBlackBoxIO(
  val pioWidth: Int
) extends Bundle {
  val odata = Input(UInt((pioWidth).W))
  val oenable = Input(UInt((pioWidth).W))
  val idata = Output(UInt((pioWidth).W))
}

class loopback(
  val pioWidth: Int
) extends BlackBox(Map(
  "pioWidth" -> core.IntParam(pioWidth)
)) with HasBlackBoxResource {
  val io = IO(new loopbackBlackBoxIO(
    pioWidth
  ))
// setResource("top.v")
}

case class loopbackParams(
  pioWidth: Int = 32,
  cacheBlockBytes: Int
)



class LloopbackBase(c: loopbackParams)(implicit p: Parameters) extends LazyModule {

  def extraResources(resources: ResourceBindings) = Map[String, Seq[ResourceValue]]()

  val device = new SimpleDevice("loopback", Seq("sifive,loopback-0.1.0")) {
    override def describe(resources: ResourceBindings): Description = {
      val Description(name, mapping) = super.describe(resources)
      Description(name, mapping ++ extraResources(resources))
    }
  }

  val pioWidth = c.pioWidth



  val ioBridgeSource = BundleBridgeSource(() => new loopbackBlackBoxIO(
    c.pioWidth
  ))

  class LloopbackBaseImp extends LazyRawModuleImp(this) {
    val blackbox = Module(new loopback(
      c.pioWidth
    ))
    // interface wiring 2

    // port wiring
    blackbox.io.odata := ioBridgeSource.bundle.odata
    blackbox.io.oenable := ioBridgeSource.bundle.oenable
    ioBridgeSource.bundle.idata := blackbox.io.idata
    // interface alias

    // interface wiring

  }
  lazy val module = new LloopbackBaseImp
}



case class NloopbackTopParams(
  blackbox: loopbackParams
) {
  def setBurstBytes(x: Int): NloopbackTopParams = this.copy()
}

object NloopbackTopParams {
  def defaults(
    cacheBlockBytes: Int
  ) = NloopbackTopParams(
    blackbox = loopbackParams(
      cacheBlockBytes = cacheBlockBytes
    )
  )
}


class NloopbackTopLogicalTreeNode(component: NloopbackTopBase) extends LogicalTreeNode(() => Some(component.imp.device)) {
  override def getOMComponents(resourceBindings: ResourceBindings, components: Seq[OMComponent]): Seq[OMComponent] = {
    DiplomaticObjectModelAddressing.getOMComponentHelper(
      resourceBindings, (resources) => {
        val name = component.imp.device.describe(resourceBindings).name
        val omDevice = new scala.collection.mutable.LinkedHashMap[String, Any] with OMDevice {
          val memoryRegions: Seq[OMMemoryRegion] =
            DiplomaticObjectModelAddressing.getOMMemoryRegions(name, resourceBindings, None)

          val interrupts: Seq[OMInterrupt] =
            DiplomaticObjectModelAddressing.describeGlobalInterrupts(name, resourceBindings)

          val _types: Seq[String] = Seq("OMloopback", "OMDevice", "OMComponent", "OMCompoundType")
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
      })
  }
}

class NloopbackTopBase(val c: NloopbackTopParams)(implicit p: Parameters)
 extends SimpleLazyModule
 with HasLogicalTreeNode {
  val imp = LazyModule(new Lloopback(c.blackbox))

  def userOM: Product with Serializable = Nil

  def logicalTreeNode: LogicalTreeNode = new NloopbackTopLogicalTreeNode(this)

  val pioWidth: Int = c.blackbox.pioWidth
// no channel node

}

object NloopbackTopBase {
  def attach(c: NloopbackTopParams)(bap: BlockAttachParams): NloopbackTop = {
    implicit val p: Parameters = bap.p
    val loopback_top = LazyModule(new NloopbackTop(c))
    // no channel attachment

    LogicalModuleTree.add(bap.parentNode, loopback_top.logicalTreeNode)
    loopback_top
  }
}

class WithloopbackTopBase (

) extends Config((site, here, up) => {
  case BlockDescriptorKey =>
    BlockDescriptor(
      name = "loopback",
      place = NloopbackTop.attach(NloopbackTopParams.defaults(
        cacheBlockBytes = site(CacheBlockBytes)
      ))
    ) +: up(BlockDescriptorKey, site)
})
