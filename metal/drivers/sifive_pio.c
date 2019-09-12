#include <stdlib.h>
#include <stdint.h>

#include "sifive_pio.h"
#include <metal/compiler.h>

void
pio_write(uint32_t *pio_base, uint32_t data, uint32_t enable)
{
  volatile uint32_t * odata   = pio_base;
  volatile uint32_t * oenable = pio_base + 1;

  *odata = data;
  *oenable = enable;
}

uint32_t
pio_read(uint32_t *pio_base)
{
  volatile uint32_t * idata   = pio_base + 2;
  return *idata;
}

void
metal_pio_write(const struct metal_pio *pio, uint32_t data, uint32_t enable)
{
    if (pio != NULL)
        pio->vtable.v_pio_write(pio->pio_base, data, enable);
}

uint32_t
metal_pio_read(const struct metal_pio *pio)
{
    return pio->vtable.v_pio_read(pio->pio_base);
}

__METAL_DEFINE_VTABLE(metal_pio) = {
    .pio_base = (uint32_t *)PIO_BASE,
    .vtable.v_pio_write   = pio_write,
    .vtable.v_pio_read    = pio_read
};

const struct metal_pio * pio_tables[] = {&metal_pio};
uint8_t pio_tables_cnt = 1;

const struct metal_pio *
get_metal_pio(uint8_t idx)
{
    if (idx >= pio_tables_cnt)
        return NULL;
    return pio_tables[idx];
}
