#include <metal/compiler.h>
#include <stdint.h>
#include <stdlib.h>

#ifndef sifive_pio0_h
#define sifive_pio0_h
#define PIO_BASE 0x60000

struct metal_pio;

struct metal_pio_vtable {
    void (*v_pio_write)(uint32_t *pio_base, uint32_t data, uint32_t enable);
    uint32_t (*v_pio_read)(uint32_t *pio_base);
};

struct metal_pio {
    uint32_t *pio_base;
    const struct metal_pio_vtable vtable;
};

__METAL_DECLARE_VTABLE(metal_pio)

void metal_pio_write(const struct metal_pio *pio, uint32_t data,
                     uint32_t enable);
uint32_t metal_pio_read(const struct metal_pio *pio);
const struct metal_pio *get_metal_pio(uint8_t idx);
#endif
