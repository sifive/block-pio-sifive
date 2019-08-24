// Generated Code
// Please DO NOT EDIT


package sifive.vip.loopback

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



class loopbackBlackBoxIO(
  val dataWidth: Int
) extends Bundle {
  val wdata = Input(UInt((dataWidth).W))
  val wenable = Input(UInt((dataWidth).W))
  val rdata = Output(UInt((dataWidth).W))
}

class loopback(
  val dataWidth: Int
) extends BlackBox(Map(
  "dataWidth" -> core.IntParam(dataWidth)
)) with HasBlackBoxResource {
  val io = IO(new loopbackBlackBoxIO(
    dataWidth
  ))
// setResource("top.v")
}

case class loopbackParams(
  dataWidth: Int = 32,
  cacheBlockBytes: Int
)



class LloopbackBase(c: loopbackParams)(implicit p: Parameters) extends LazyModule {
  val device = new SimpleDevice("loopback", Seq("sifive,loopback-v0"))

  val dataWidth = c.dataWidth



  val ioBridgeSource = BundleBridgeSource(() => new loopbackBlackBoxIO(
    c.dataWidth
  ))

  class LloopbackBaseImp extends LazyRawModuleImp(this) {
    val blackbox = Module(new loopback(
      c.dataWidth
    ))
    // interface wiring 2

    // port wiring
    blackbox.io.wdata := ioBridgeSource.bundle.wdata
    blackbox.io.wenable := ioBridgeSource.bundle.wenable
    ioBridgeSource.bundle.rdata := blackbox.io.rdata
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

class NloopbackTopBase(c: NloopbackTopParams)(implicit p: Parameters) extends SimpleLazyModule {
  val imp = LazyModule(new Lloopback(c.blackbox))
  val dataWidth: Int = c.blackbox.dataWidth
// no channel node

}

object NloopbackTopBase {
  def attach(c: NloopbackTopParams)(bap: BlockAttachParams): NloopbackTop = {
    implicit val p: Parameters = bap.p
    val loopback_top = LazyModule(new NloopbackTop(c))
    // no channel attachment

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
