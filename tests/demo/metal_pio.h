#include <stdlib.h>
#include <stdint.h>
#include <metal/compiler.h>


struct metal_pio;

struct metal_pio_vtable {
  void     (*v_pio_write)(uint32_t data, uint32_t enable);
  uint32_t (*v_pio_read)(void);
};

struct metal_pio {
    const struct metal_pio_vtable vtable;
};

__METAL_DECLARE_VTABLE(metal_pio)

extern void metal_pio_write(const struct metal_pio *pio, uint32_t data, uint32_t enable);
extern uint32_t metal_pio_read(const struct metal_pio *pio);
extern const struct metal_pio * get_metal_pio(void);

