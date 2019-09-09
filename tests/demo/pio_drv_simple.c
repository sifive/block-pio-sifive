#include <stdlib.h>
#include <stdint.h>
#include "pio_drv_simple.h"

void
pio_write(uint32_t data, uint32_t enable)
{
  volatile uint32_t * odata   = (uint32_t *) PIO_BASE;
  volatile uint32_t * oenable = (uint32_t *) (PIO_BASE + sizeof(uint32_t));

  *odata = data;
  *oenable = enable;
}

uint32_t
pio_read()
{
  volatile uint32_t * idata   = (uint32_t *)(PIO_BASE + 2 * sizeof(uint32_t));
  return *idata;
}
