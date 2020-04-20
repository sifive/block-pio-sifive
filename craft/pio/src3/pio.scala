// USER editable file


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



class Lpio(c: pioParams)(implicit p: Parameters) extends LpioBase(c)(p)
{

// User code here

}

class NpioTop(c: NpioTopParams)(implicit p: Parameters) extends NpioTopBase(c)(p)
{

  // User code here

  // This is the code the tutorial tells me to add

  // add in custom fields to the Object Model entry for this block
  override val userOM: OMPIO = OMPIO(c.blackbox.pioWidth)

  // associate register maps with memory regions in Object model
  override def getOMMemoryRegions(resourceBindings: ResourceBindings) = {
    super.getOMMemoryRegions(resourceBindings).zip(omRegisterMaps).map { case (memRegion, regmap) =>
      memRegion.copy(registerMap = Some(regmap))
    }
  }
}

object NpioTop {
  def attach(c: NpioTopParams)(bap: BlockAttachParams): NpioTop = {
    val pio = NpioTopBase.attach(c)(bap)


    // User code here

    pio
  }
}

class WithpioTop extends Config(
  new WithpioTopBase(
    t_ctrl_base = 0x1000000000000L
  )

    // User code here
)


// This is the code the tutorial tells me to add
case class OMPIO(width: Int)

