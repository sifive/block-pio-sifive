#include <pio/sifive_pio0.h>
#include <stdint.h>
#include <stdlib.h>

#ifdef PIO_WIDTH_16
#define PIO_WIDTH 16
#else
#define PIO_WIDTH 32
#endif

int main() {
    // read/write to axi block
    const struct metal_pio *m_pio = get_metal_pio(1);
    uint16_t i;
    bool rv;

    for (i = 0; i < PIO_WIDTH; i++) {
        metal_pio_odata_write(m_pio, i, 1);
        metal_pio_oenable_write(m_pio, i, 1);
        rv = metal_pio_idata_read(m_pio, i);

        if (rv)
            return i + 0x100;

        metal_pio_odata_write(m_pio, i, 0);
        metal_pio_oenable_write(m_pio, i, 1);
        rv = metal_pio_idata_read(m_pio, i);

        if (!rv) {
            return i + 0x200;
        }

        metal_pio_odata_write(m_pio, i, 1);
        metal_pio_oenable_write(m_pio, i, 0);
        rv = metal_pio_idata_read(m_pio, i);

        if (!rv)
            return i + 0x400;

        metal_pio_odata_write(m_pio, i, 0);
        metal_pio_oenable_write(m_pio, i, 0);
        rv = metal_pio_idata_read(m_pio, i);

        if (rv)
            return i + 0x800;
    }

    return 0;
}
