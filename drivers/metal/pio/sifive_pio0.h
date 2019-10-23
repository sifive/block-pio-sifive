
#include <metal/compiler.h>
#include <metal/io.h>
#include <stdint.h>
#include <stdlib.h>

typedef uint8_t bool;

// : these macros have control_base as a hidden input
#define METAL_PIO_REG(offset) (((__metal_io_u8 *)control_base + offset))
#define METAL_PIO_REGW(offset)                                                 \
    (__METAL_ACCESS_ONCE((__metal_io_u8 *)METAL_PIO_REG(offset)))

#define METAL_PIO_ODATA 0
#define METAL_PIO_OENABLE 1
#define METAL_PIO_IDATA 2

struct metal_pio;

struct metal_pio_vtable {
    void (*v_pio_odata_write)(uint8_t *pio_base, uint16_t width, uint16_t bit,
                              bool data);
    void (*v_pio_oenable_write)(uint8_t *pio_base, uint16_t width, uint16_t bit,
                                bool data);
    bool (*v_pio_idata_read)(uint8_t *pio_base, uint16_t width, uint16_t bit);
};

struct metal_pio {
    uint8_t *pio_base;
    uint16_t pio_width;
    const struct metal_pio_vtable vtable;
};

void metal_pio_odata_write(const struct metal_pio *pio, uint16_t bit,
                           bool data);
void metal_pio_oenable_write(const struct metal_pio *pio, uint16_t bit,
                             bool data);
bool metal_pio_idata_read(const struct metal_pio *pio, uint16_t bit);
const struct metal_pio *get_metal_pio(uint8_t index);
