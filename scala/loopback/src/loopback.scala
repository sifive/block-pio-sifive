// USER editable file


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



class Lloopback(c: loopbackParams)(implicit p: Parameters) extends LloopbackBase(c)(p)
{

// User code here

}

class NloopbackTop(c: NloopbackTopParams)(implicit p: Parameters) extends NloopbackTopBase(c)(p)
{

// User code here

}

object NloopbackTop {
  def attach(c: NloopbackTopParams)(bap: BlockAttachParams): NloopbackTop = {
    val loopback = NloopbackTopBase.attach(c)(bap)

    // User code here

    loopback
  }
}

class WithloopbackTop extends Config(
  new WithloopbackTopBase(

  )

    // User code here
)
