// `timescale 1ns/1ns

module tb;
  logic clk, reset_n;

  logic awvalid, awready;
  logic [31:0] awaddr;
  logic [2:0] awprot;

  logic wvalid, wready;
  logic [31:0] wdata;
  logic [3:0] wstrb;

  logic bvalid, bready;
  logic [1:0] bresp;

  logic arvalid, arready;
  logic [31:0] araddr;
  logic [2:0] arprot;

  logic rvalid, rready;
  logic [31:0] rdata;
  logic [1:0] rresp;

  logic [9:0] odata;
  logic [9:0] oenable;
  logic [9:0] idata;

  pio dut(
    .t_ctrl_awvalid(awvalid),
    .t_ctrl_awready(awready),
    .t_ctrl_awaddr(awaddr),
    .t_ctrl_awprot(awprot),

    .t_ctrl_wvalid(wvalid),
    .t_ctrl_wready(wready),
    .t_ctrl_wdata(wdata),
    .t_ctrl_wstrb(wstrb),

    .t_ctrl_bvalid(bvalid),
    .t_ctrl_bready(bready),
    .t_ctrl_bresp(bresp),

    .t_ctrl_arvalid(arvalid),
    .t_ctrl_arready(arready),
    .t_ctrl_araddr(araddr),
    .t_ctrl_arprot(arprot),

    .t_ctrl_rvalid(rvalid),
    .t_ctrl_rready(rready),
    .t_ctrl_rdata(rdata),
    .t_ctrl_rresp(rresp),

    .irq0(irq0),
    .irq1(irq1),

    .odata(odata),
    .oenable(oenable),
    .idata(idata),

    .clk(clk),
    .reset_n(reset_n)
  );

  initial begin
    $dumpfile("tb.vcd");
    $dumpvars(1);
    $display("start time = %5d ns", $time);
    reset_n <= 0;
    toggle_clk;
    reset_n <= 1;
    toggle_clk;
    toggle_clk;
    toggle_clk;
    toggle_clk;
    $display("stop  time = %5d ns", $time);
  end

  task toggle_clk;
    begin
      #10 clk = ~clk;
      #10 clk = ~clk;
    end
  endtask

  // `ifdef VCS
  // final begin
  //   $vcdplusflush();
  // end
  // `endif

endmodule
