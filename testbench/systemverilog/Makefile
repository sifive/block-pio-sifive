vcs:
	vcs \
	-quiet \
	-full64 \
	-timescale=1ns/10ps \
	+vcs+flush+all \
	+warn=all \
	-sverilog \
	../../rtl/verilog/pio/pio.sv \
	tb.sv

run: vcs
	./simv \
	+max-cycles=1000

clean:
	rm -rf simv* csrc *.vcd

all: run
