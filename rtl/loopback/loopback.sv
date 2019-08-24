module loopback #(
  parameter dataWidth = 8
) (
  input  [dataWidth-1:0] wdata,
  input  [dataWidth-1:0] wenable,
  output [dataWidth-1:0] rdata
);

assign rdata = wdata ^ wenable;

endmodule
