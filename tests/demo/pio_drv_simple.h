#include <stdlib.h>
#include <stdint.h>

#define PIO_BASE 0x60000

extern void pio_write(uint32_t data, uint32_t enable);
extern uint32_t pio_read();
