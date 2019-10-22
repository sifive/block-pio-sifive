vcs:
	vcs \
	-full64 \
	-timescale=1ns/10ps \
	+vcs+flush+all \
	+warn=all \
	-sverilog \
	../rtl/pio/pio.sv \
	-debug_access+pp \
	tb.sv

run: vcs
	./simv \
	+max-cycles=1000

clean:
	rm -rf simv* csrc *.vcd

all: run
