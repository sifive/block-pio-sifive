package sifive.blocks.MyTestMonitorAXI4

import chisel3._
import chisel3.core.Reset
import freechips.rocketchip.amba.axi4.{AXI4Bundle, AXI4EdgeParameters, AXI4MonitorArgs, AXI4MonitorBase}

class MyTestMonitorAXI4Monitor(params: AXI4MonitorArgs) extends MyTestMonitorAXI4MonitorBase(params) {
  // IMPLEMENT THIS METHOD: convert the input parameters to blackbox parameters
  def getBlackBoxParams(edgeParams: AXI4EdgeParameters): MyTestMonitorAXI4Params = {
    MyTestMonitorAXI4Params(edgeParams.bundle.addrBits, edgeParams.slave.beatBytes * 8)
  }

  // if you need to override the default connnection method: uncomment and implement this method
  /*
  override def connectBlackBoxPorts(
    blackbox: MyTestMonitorAXI4BlackBox,
    bundle: AXI4Bundle,
    edgeParams: AXI4EdgeParameters,
    reset: Reset): Unit = {
  }
  */
}
