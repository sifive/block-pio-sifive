// Generated Code
// Please DO NOT EDIT
package sifive.blocks.MyTestMonitorAXI4

import chisel3._
import chisel3.core.Reset
import freechips.rocketchip.amba.axi4.{AXI4Bundle, AXI4EdgeParameters, AXI4MonitorArgs, AXI4MonitorBase}

case class MyTestMonitorAXI4Params(
  addrWidth: Int = 12,
  dataWidth: Int = 32
)

class MyTestMonitorAXI4BlackBoxIO(
  val addrWidth: Int,
  val dataWidth: Int
) extends Bundle {
  val t_ctrl_awvalid = Input(Bool())
  val t_ctrl_awready = Input(Bool())
  val t_ctrl_awaddr = Input(UInt((addrWidth).W))
  val t_ctrl_awprot = Input(UInt((3).W))
  val t_ctrl_wvalid = Input(Bool())
  val t_ctrl_wready = Input(Bool())
  val t_ctrl_wdata = Input(UInt((dataWidth).W))
  val t_ctrl_wstrb = Input(UInt((dataWidth / 8).W))
  val t_ctrl_bvalid = Input(Bool())
  val t_ctrl_bready = Input(Bool())
  val t_ctrl_bresp = Input(UInt((2).W))
  val t_ctrl_arvalid = Input(Bool())
  val t_ctrl_arready = Input(Bool())
  val t_ctrl_araddr = Input(UInt((addrWidth).W))
  val t_ctrl_arprot = Input(UInt((3).W))
  val t_ctrl_rvalid = Input(Bool())
  val t_ctrl_rready = Input(Bool())
  val t_ctrl_rdata = Input(UInt((dataWidth).W))
  val t_ctrl_rresp = Input(UInt((2).W))
  val reset = Input(Bool())
  val clk = Input(Bool())
}

class MyTestMonitorAXI4BlackBox(
  val addrWidth: Int,
  val dataWidth: Int
) extends BlackBox(Map(
  "addrWidth" -> core.IntParam(addrWidth),
  "dataWidth" -> core.IntParam(dataWidth)
)) {
  val io = IO(new MyTestMonitorAXI4BlackBoxIO(
    addrWidth = addrWidth,
    dataWidth = dataWidth
  ))

  override val desiredName = "MyTestMonitorAXI4"
}

abstract class MyTestMonitorAXI4MonitorBase(params: AXI4MonitorArgs) extends AXI4MonitorBase(params) {
  def getBlackBoxParams(edgeParams: AXI4EdgeParameters): MyTestMonitorAXI4Params

  def connectBlackBoxPorts(
    blackbox: MyTestMonitorAXI4BlackBox,
    bundle: AXI4Bundle,
    edgeParams: AXI4EdgeParameters,
    reset: Reset): Unit = {
    blackbox.io.t_ctrl_awvalid := bundle.aw.valid
    blackbox.io.t_ctrl_awready := bundle.aw.ready
    blackbox.io.t_ctrl_awaddr := bundle.aw.bits.addr
    blackbox.io.t_ctrl_awprot := bundle.aw.bits.prot
    blackbox.io.t_ctrl_wvalid := bundle.w.valid
    blackbox.io.t_ctrl_wready := bundle.w.ready
    blackbox.io.t_ctrl_wdata := bundle.w.bits.data
    blackbox.io.t_ctrl_wstrb := bundle.w.bits.strb
    blackbox.io.t_ctrl_bvalid := bundle.b.valid
    blackbox.io.t_ctrl_bready := bundle.b.ready
    blackbox.io.t_ctrl_bresp := bundle.b.bits.resp
    blackbox.io.t_ctrl_arvalid := bundle.ar.valid
    blackbox.io.t_ctrl_arready := bundle.ar.ready
    blackbox.io.t_ctrl_araddr := bundle.ar.bits.addr
    blackbox.io.t_ctrl_arprot := bundle.ar.bits.prot
    blackbox.io.t_ctrl_rvalid := bundle.r.valid
    blackbox.io.t_ctrl_rready := bundle.r.ready
    blackbox.io.t_ctrl_rdata := bundle.r.bits.data
    blackbox.io.t_ctrl_rresp := bundle.r.bits.resp
  }

  def legalize(
    bundle: AXI4Bundle,
    edgeParams: AXI4EdgeParameters,
    reset: Reset): Unit = {
    val params = getBlackBoxParams(edgeParams)
    val blackbox = Module(new MyTestMonitorAXI4BlackBox(
      addrWidth = params.addrWidth,
      dataWidth = params.dataWidth
    ))
    connectBlackBoxPorts(blackbox, bundle, edgeParams, reset)
  }
}
