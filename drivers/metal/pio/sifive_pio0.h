
#include <metal/compiler.h>
#include <metal/io.h>
#include <bsp_pio/sifive_pio.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdbool.h>

#define METAL_PIO_REGB(offset) \
   (__METAL_ACCESS_ONCE((uint8_t *)METAL_PIO_REG(offset)))

struct metal_pio_vtable {
    void (*v_pio_odata_write)(uint8_t *pio_base, uint16_t bit, bool data);
    void (*v_pio_oenable_write)(uint8_t *pio_base, uint16_t bit, bool data);
    bool (*v_pio_idata_read)(uint8_t *pio_base, uint16_t bit);
};

struct metal_pio {
    uint8_t *pio_base;
    uint16_t pio_width;
    struct metal_pio_vtable vtable;
};

void metal_pio_odata_write(const struct metal_pio *pio, uint16_t bit,
                           bool data);
void metal_pio_oenable_write(const struct metal_pio *pio, uint16_t bit,
                             bool data);
bool metal_pio_idata_read(const struct metal_pio *pio, uint16_t bit);
const struct metal_pio *get_metal_pio(uint8_t index);
