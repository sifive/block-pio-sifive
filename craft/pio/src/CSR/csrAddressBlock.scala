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

abstract class csrAddressBlockRegRouter(busWidthBytes: Int, baseAddress: Long)(implicit p: Parameters)
  extends IORegisterRouter(
    RegisterRouterParams(
      name = "csrAddressBlock",
      compat = Seq.empty,
      base = baseAddress,
      beatBytes = busWidthBytes),
    new csrAddressBlockAddressBlockBundle) {

  private def regFieldWriteOnceFn(register: UInt): RegWriteFn = {
    val written = RegInit(false.B)
    RegWriteFn((valid, data) => {
      when (valid && !written) {
        register := data
        written := true.B
      }
      true.B
    })
  }

  lazy val module = new LazyModuleImp(this) {
    val portValue = ioNode.makeIO()
    val register = RegInit(0.U.asTypeOf(portValue.cloneType.asOutput))
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

class csrAddressBlockAXI4RegMap(busWidthBytes: Int, baseAddress: Long)(implicit p: Parameters)
  extends csrAddressBlockRegRouter(busWidthBytes, baseAddress) with HasAXI4ControlRegMap
