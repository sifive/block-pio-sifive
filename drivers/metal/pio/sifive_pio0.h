
#include <metal/compiler.h>
#include <stdint.h>
#include <stdlib.h>
#include <bsp_pio/sifive_pio.h>

#ifndef sifive_pio0_h
#define sifive_pio0_h

struct metal_pio;

struct metal_pio_vtable {
    void (*v_pio_odata_write)(uint32_t * pio_base, uint32_t data);
    uint32_t (*v_pio_odata_read)(uint32_t  *pio_base);
    void (*v_pio_oenable_write)(uint32_t * pio_base, uint32_t data);
    uint32_t (*v_pio_oenable_read)(uint32_t  *pio_base);
    void (*v_pio_idata_write)(uint32_t * pio_base, uint32_t data);
    uint32_t (*v_pio_idata_read)(uint32_t  *pio_base);
};

struct metal_pio {
    uint32_t *pio_base;
    const struct metal_pio_vtable vtable;
};

__METAL_DECLARE_VTABLE(metal_pio)

void metal_pio_odata_write(const struct metal_pio *pio, uint32_t data);
uint32_t metal_pio_odata_read(const struct metal_pio *pio);
void metal_pio_oenable_write(const struct metal_pio *pio, uint32_t data);
uint32_t metal_pio_oenable_read(const struct metal_pio *pio);
void metal_pio_idata_write(const struct metal_pio *pio, uint32_t data);
uint32_t metal_pio_idata_read(const struct metal_pio *pio);
const struct metal_pio *get_metal_pio(uint8_t index);
#endif
