// Parallel Input Output (PIO) block
module pio #(
  parameter dataWidth = 32
) (
  input  [dataWidth-1:0] in_wdata,
  input  [dataWidth-1:0] in_wenable,
  output [dataWidth-1:0] in_rdata,

  output [dataWidth-1:0] out_wdata,
  output [dataWidth-1:0] out_wenable,
  input  [dataWidth-1:0] out_rdata,

  output logic irq0,
  output logic irq1
);

assign out_wdata   = in_wdata;
assign out_wenable = in_wenable;
assign in_rdata    = out_rdata;

// IRQ
assign irq0 = 1'b0;//|idata;
assign irq1 = 1'b0;//|idata;

endmodule
