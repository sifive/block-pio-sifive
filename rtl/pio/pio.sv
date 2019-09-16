// Parallel Input Output (PIO) block
module pio #(
    parameter addrWidth = 32,
    parameter dataWidth = 32,
    parameter pioWidth = 10, // <= dataWidth
    parameter writeStrobeWidth = 4 // dataWidth / 8
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
    input  [writeStrobeWidth-1:0] t_ctrl_wstrb,
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
    // Interrupts
    output logic                  irq0,
    output logic                  irq1,
    // IO pins
    output logic   [pioWidth-1:0] odata,
    output logic   [pioWidth-1:0] oenable,
    input          [pioWidth-1:0] idata,
    // system
    input clk,
    input reset_n
);


// Write FSM

// t_ctrl_awvalid     t_ctrl_wvalid ->> t_ctrl_bvalid
//       \         x         \       /            \
//   t_ctrl_awready  t_ctrl_wready             t_ctrl_bready

localparam  W_I = 3'b001;
localparam  W_A = 3'b010;
localparam  W_D = 3'b100;

logic [2:0] w_state, w_state_nxt;

// transition conditions
logic w_i_a, w_i_d, w_a_d, w_d_i;

assign w_i_d = t_ctrl_awvalid & t_ctrl_awready & t_ctrl_wvalid & t_ctrl_wready;
assign w_i_a = t_ctrl_awvalid & t_ctrl_awready;
assign w_a_d = t_ctrl_wvalid & t_ctrl_wready;
assign w_d_i = t_ctrl_bvalid & t_ctrl_bready;

assign w_state_nxt =
  ((w_state == W_I) & w_i_d) ? W_D :
  ((w_state == W_I) & w_i_a) ? W_A :
  ((w_state == W_A) & w_a_d) ? W_D :
  ((w_state == W_D) & w_d_i) ? W_I : w_state;

always @(posedge clk or negedge reset_n)
  if (!reset_n) w_state <= W_I;
  else          w_state <= w_state_nxt;

// AW

logic awe;
logic [1:0] awaddr;

assign awe = t_ctrl_awvalid & t_ctrl_awready;

always @(posedge clk or negedge reset_n)
    if (~reset_n) awaddr <= 2'b00;
    else if (awe) awaddr <= t_ctrl_awaddr[3:2];

assign t_ctrl_awready = (w_state == W_I);

// W
logic owe, oee;
logic [pioWidth-1:0] wdata;

assign owe = t_ctrl_wvalid & t_ctrl_wready & (awe ? (t_ctrl_awaddr[3:2] == 2'b00) : (awaddr == 2'b00));
assign oee = t_ctrl_wvalid & t_ctrl_wready & (awe ? (t_ctrl_awaddr[3:2] == 2'b01) : (awaddr == 2'b01));
assign wdata = t_ctrl_wdata[pioWidth-1:0];

// FIXME use t_ctrl_wstrb
always @(posedge clk or negedge reset_n)
    if (~reset_n) odata <= {pioWidth{1'b0}};
    else if (owe) odata <= wdata;

always @(posedge clk or negedge reset_n)
    if (~reset_n) oenable <= {pioWidth{1'b0}};
    else if (oee) oenable <= wdata;

assign t_ctrl_wready = (w_state == W_I) | (w_state == W_A);

// B
assign t_ctrl_bresp = 2'b00; // OKAY <- [OKAY, EXOKAY, SLVERR, DECERR]
assign t_ctrl_bvalid = (w_state == W_D);


// Read FSM

// t_ctrl_arvalid ->> t_ctrl_rvalid
//            \         //        \
//          t_ctrl_arready       t_ctrl_rready

localparam  R_I = 2'b01;
localparam  R_D = 2'b10;

logic [1:0] r_state, r_state_nxt;

// transition conditions
logic r_i_d, r_d_i;

assign r_i_d = t_ctrl_arvalid & t_ctrl_arready;
assign r_d_i = t_ctrl_rvalid & t_ctrl_rready;

assign r_state_nxt =
  ((r_state == R_I) & r_i_d) ? R_D :
  ((r_state == R_D) & r_d_i) ? R_I : r_state;

always @(posedge clk or negedge reset_n)
  if (!reset_n) r_state <= R_I;
  else          r_state <= r_state_nxt;

// AR
logic are;
logic [1:0] araddr;

assign are = t_ctrl_arvalid & t_ctrl_arready;

always @(posedge clk or negedge reset_n)
    if (~reset_n) araddr <= 2'b00;
    else if (are) araddr <= t_ctrl_araddr[3:2];

assign t_ctrl_arready = (r_state == R_I);

// R

logic [pioWidth - 1:0] rdata;

assign rdata = (
    (araddr == 2'b00) ? odata :
    (araddr == 2'b01) ? oenable :
    (araddr == 2'b10) ? idata : idata
);

assign t_ctrl_rdata = {{(dataWidth - pioWidth){1'b0}}, rdata};

assign t_ctrl_rvalid = (r_state == R_D);

// IRQ
assign irq0 = 1'b0;//|idata;
assign irq1 = &idata;

endmodule
