
#include <stdint.h>
#include <stdlib.h>

#include <metal/compiler.h>
#include <metal/io.h>
#include <pio/sifive_pio0.h>

// base reads/write

void pio_odata_write(uint8_t *pio_base, uint16_t width, uint16_t bit,
                     bool data) {
    volatile uint8_t *control_base = pio_base;

    unsigned int byte_to_write = (bit >> 3) + (width * METAL_PIO_ODATA);
    uint8_t bit_to_write = 1 << (bit & 0x0007);

    if (data) {
        METAL_PIO_REGW(byte_to_write) |= bit_to_write;
    } else {
        METAL_PIO_REGW(byte_to_write) &= ~bit_to_write;
    }
}

void pio_oenable_write(uint8_t *pio_base, uint16_t width, uint16_t bit,
                       bool data) {
    volatile uint8_t *control_base = pio_base;

    unsigned int byte_to_write = (bit >> 3) + (width * METAL_PIO_OENABLE);
    uint8_t bit_to_write = 1 << (bit & 0x0007);
    if (data) {
        METAL_PIO_REGW(byte_to_write) |= bit_to_write;
    } else {
        METAL_PIO_REGW(byte_to_write) &= ~bit_to_write;
    }
}

bool pio_idata_read(uint8_t *pio_base, uint16_t width, uint16_t bit) {
    volatile uint8_t *control_base = pio_base;
    unsigned int byte_to_read = (bit >> 3) + (width * METAL_PIO_IDATA);
    uint8_t bit_to_read = 1 << (bit & 0x0007);
    return !!(METAL_PIO_REGW(byte_to_read) & bit_to_read);
}

// metal read writes

void metal_pio_odata_write(const struct metal_pio *pio, uint16_t bit,
                           bool data) {
    if (pio != NULL)
        pio->vtable.v_pio_odata_write(pio->pio_base, pio->pio_width, bit, data);
}

void metal_pio_oenable_write(const struct metal_pio *pio, uint16_t bit,
                             bool data) {
    if (pio != NULL)
        pio->vtable.v_pio_oenable_write(pio->pio_base, pio->pio_width, bit,
                                        data);
}

bool metal_pio_idata_read(const struct metal_pio *pio, uint16_t bit) {
    if (pio != NULL)
        return pio->vtable.v_pio_idata_read(pio->pio_base, pio->pio_width, bit);
    return 0;
}

/* This creation would be done dynamically for truly parameterized
 * system. Each instance of the device would have a different pio_base
 * and it's own pio_width, but the vtable would remain the same in
 * each instance.
 */

const struct metal_pio metal_pio1 = {
    .pio_base = (uint8_t *)PIO_BASE,
    .pio_width = 32 / 8,
    .vtable.v_pio_odata_write = pio_odata_write,
    .vtable.v_pio_oenable_write = pio_oenable_write,
    .vtable.v_pio_idata_read = pio_idata_read,
};

const struct metal_pio metal_pio2 = {
    .pio_base = (uint8_t *)PIO_BASE,
    .pio_width = 32 / 8,
    .vtable.v_pio_odata_write = pio_odata_write,
    .vtable.v_pio_oenable_write = pio_oenable_write,
    .vtable.v_pio_idata_read = pio_idata_read,
};

const struct metal_pio *pio_tables[] = {&metal_pio1, &metal_pio2};
uint8_t pio_tables_cnt = 2;

const struct metal_pio *get_metal_pio(uint8_t idx) {
    if (idx >= pio_tables_cnt)
        return NULL;
    return pio_tables[idx];
}
