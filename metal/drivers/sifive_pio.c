#include <stdint.h>
#include <stdlib.h>

#include <pio/sifive_pio0.h>
#include <metal/compiler.h>
#include <metal/io.h>

// Note: these macros have control_base as a hidden input
#define METAL_PIO_REG(offset) (((unsigned long)control_base + offset))
#define METAL_PIO_REGW(offset)                                                 \
    (__METAL_ACCESS_ONCE((__metal_io_u32 *)METAL_PIO_REG(offset)))

#define METAL_PIO_DATA 0
#define METAL_PIO_ENABLE 4
#define METAL_PIO_READ 8

void pio_write(uint32_t *pio_base, uint32_t data, uint32_t enable) {
    volatile uint32_t *control_base = pio_base;

    METAL_PIO_REGW(METAL_PIO_DATA) = data;
    METAL_PIO_REGW(METAL_PIO_ENABLE) = enable;
}

uint32_t pio_read(uint32_t *pio_base) {
    volatile uint32_t *control_base = pio_base;
    return METAL_PIO_REGW(METAL_PIO_READ);
}

void metal_pio_write(const struct metal_pio *pio, uint32_t data,
                     uint32_t enable) {
    if (pio != NULL)
        pio->vtable.v_pio_write(pio->pio_base, data, enable);
}

uint32_t metal_pio_read(const struct metal_pio *pio) {
    if (pio != NULL)
        return pio->vtable.v_pio_read(pio->pio_base);
    return (uint32_t)-1;
}

__METAL_DEFINE_VTABLE(metal_pio) = {.pio_base = (uint32_t *)PIO_BASE,
                                    .vtable.v_pio_write = pio_write,
                                    .vtable.v_pio_read = pio_read};

const struct metal_pio *pio_tables[] = {&metal_pio};
uint8_t pio_tables_cnt = 1;

const struct metal_pio *get_metal_pio(uint8_t idx) {
    if (idx >= pio_tables_cnt)
        return NULL;
    return pio_tables[idx];
}
