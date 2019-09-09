#include <stdlib.h>
#include <stdint.h>

#include "pio_drv_simple.h"
#include "metal_pio.h"
#include <metal/compiler.h>

void
metal_pio_write(const struct metal_pio *pio, uint32_t data, uint32_t enable)
{
    if (pio !=NULL)
        pio->vtable.v_pio_write(data, enable);
}

uint32_t
metal_pio_read(const struct metal_pio *pio)
{
    return pio->vtable.v_pio_read();
}

__METAL_DEFINE_VTABLE(metal_pio) = {
    .vtable.v_pio_write   = pio_write,
    .vtable.v_pio_read    = pio_read
    };

