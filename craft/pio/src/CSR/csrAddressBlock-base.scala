// Generated Code
// Please DO NOT EDIT


package sifive.blocks.pio.CSR.csrAddressBlock

import chisel3._

import freechips.rocketchip.config.Parameters
import freechips.rocketchip.diplomacy.LazyModuleImp
import freechips.rocketchip.regmapper._
import freechips.rocketchip.tilelink.HasTLControlRegMap
import freechips.rocketchip.amba.axi4.HasAXI4ControlRegMap

class ODATARegisterBundle extends Bundle {
  val data = Output(UInt(32.W))
}

class OENABLERegisterBundle extends Bundle {
  val data = Output(UInt(32.W))
}

class IDATARegisterBundle extends Bundle {
  val data = Input(UInt(32.W))
}

class csrAddressBlockAddressBlockBundle extends Bundle {
  val ODATA = new ODATARegisterBundle()
  val OENABLE = new OENABLERegisterBundle()
  val IDATA = new IDATARegisterBundle()
}

object csrAddressBlockRegRouterBase {
  def deviceName: String = "pio-csrAddressBlock"
  def deviceCompat: Seq[String] = Seq("sifive,pio-0.1.0")
}

abstract class csrAddressBlockRegRouter(busWidthBytes: Int, baseAddress: Long)(implicit p: Parameters)
  extends IORegisterRouter(
    RegisterRouterParams(
      name = csrAddressBlockRegRouterBase.deviceName,
      compat = csrAddressBlockRegRouterBase.deviceCompat,
      base = baseAddress,
      beatBytes = busWidthBytes),
    new csrAddressBlockAddressBlockBundle) {

  lazy val module = new LazyModuleImp(this) {
    val portValue = ioNode.makeIO()
    val resetValue = Wire(portValue.cloneType.asOutput)
    resetValue.ODATA.data := 0.U
    resetValue.OENABLE.data := 0.U
    resetValue.IDATA.data := 0.U

    val register = RegInit(resetValue)
    portValue <> register

    val mapping = Seq(
      0 -> RegFieldGroup("ODATA", None, Seq(
        RegField(32, register.ODATA.data, RegFieldDesc("data", "")))),
      4 -> RegFieldGroup("OENABLE", Some("determines whether the pin is an input or an output. If the data direction bit is a 1, then the pin is an input"), Seq(
        RegField(32, register.OENABLE.data, RegFieldDesc("data", "")))),
      8 -> RegFieldGroup("IDATA", Some("read the port pins"), Seq(
        RegField.r(32, register.IDATA.data, RegFieldDesc("data", "")))))
    regmap(mapping:_*)
  }
}

class csrAddressBlockTLRegMap(busWidthBytes: Int, baseAddress: Long)(implicit p: Parameters)
  extends csrAddressBlockRegRouter(busWidthBytes, baseAddress) with HasTLControlRegMap

class csrAddressBlockAXI4RegMapAXI4RegMap(busWidthBytes: Int, baseAddress: Long)(implicit p: Parameters)
  extends csrAddressBlockRegRouter(busWidthBytes, baseAddress) with HasAXI4ControlRegMap
