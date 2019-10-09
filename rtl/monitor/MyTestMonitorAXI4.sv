module MyTestMonitorAXI4 #(
    parameter addrWidth = 32,
    parameter dataWidth = 32
) (
    // AXI4-Lite
    // AW
    input                         t_ctrl_awvalid,
    output logic                  t_ctrl_awready,
    input         [addrWidth-1:0] t_ctrl_awaddr,
    input                   [2:0] t_ctrl_awprot, // ignored
    // W
    input                         t_ctrl_wvalid,
    output logic                  t_ctrl_wready,
    input         [dataWidth-1:0] t_ctrl_wdata,
    input  [dataWidth-1:0] t_ctrl_wstrb,
    // B
    output logic                  t_ctrl_bvalid,
    input                         t_ctrl_bready,
    output logic            [1:0] t_ctrl_bresp,
    // AR
    input                         t_ctrl_arvalid,
    output logic                  t_ctrl_arready,
    input         [addrWidth-1:0] t_ctrl_araddr,
    input                   [2:0] t_ctrl_arprot,
    // R
    output logic                  t_ctrl_rvalid,
    input                         t_ctrl_rready,
    output logic  [dataWidth-1:0] t_ctrl_rdata,
    output logic            [1:0] t_ctrl_rresp,

    // system
    input clk,
    input reset
);

endmodule
