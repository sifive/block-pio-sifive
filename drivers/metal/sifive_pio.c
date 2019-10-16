
#include <stdint.h>
#include <stdlib.h>

#include <pio/sifive_pio0.h>
#include <metal/compiler.h>
#include <metal/io.h>


void pio_odata_write(uint32_t *pio_base, uint32_t data)
{
    volatile uint32_t *control_base = pio_base;
    METAL_PIO_REGW(METAL_PIO_ODATA) = data;
}


uint32_t pio_odata_read(uint32_t *pio_base)
{
    volatile uint32_t *control_base = pio_base;
    return METAL_PIO_REGW(METAL_PIO_ODATA);
}


void pio_oenable_write(uint32_t *pio_base, uint32_t data)
{
    volatile uint32_t *control_base = pio_base;
    METAL_PIO_REGW(METAL_PIO_OENABLE) = data;
}


uint32_t pio_oenable_read(uint32_t *pio_base)
{
    volatile uint32_t *control_base = pio_base;
    return METAL_PIO_REGW(METAL_PIO_OENABLE);
}


void pio_idata_write(uint32_t *pio_base, uint32_t data)
{
    volatile uint32_t *control_base = pio_base;
    METAL_PIO_REGW(METAL_PIO_IDATA) = data;
}


uint32_t pio_idata_read(uint32_t *pio_base)
{
    volatile uint32_t *control_base = pio_base;
    return METAL_PIO_REGW(METAL_PIO_IDATA);
}



void metal_pio_odata_write(const struct metal_pio *pio, uint32_t data)
{
    if (pio != NULL)
        pio->vtable.v_pio_odata_write(pio->pio_base, data);
}


uint32_t metal_pio_odata_read(const struct metal_pio *pio)
{
    if (pio != NULL)
        return pio->vtable.v_pio_odata_read(pio->pio_base);
    return (uint32_t)-1;
}


void metal_pio_oenable_write(const struct metal_pio *pio, uint32_t data)
{
    if (pio != NULL)
        pio->vtable.v_pio_oenable_write(pio->pio_base, data);
}


uint32_t metal_pio_oenable_read(const struct metal_pio *pio)
{
    if (pio != NULL)
        return pio->vtable.v_pio_oenable_read(pio->pio_base);
    return (uint32_t)-1;
}


void metal_pio_idata_write(const struct metal_pio *pio, uint32_t data)
{
    if (pio != NULL)
        pio->vtable.v_pio_idata_write(pio->pio_base, data);
}


uint32_t metal_pio_idata_read(const struct metal_pio *pio)
{
    if (pio != NULL)
        return pio->vtable.v_pio_idata_read(pio->pio_base);
    return (uint32_t)-1;
}


__METAL_DEFINE_VTABLE(metal_pio) = {
    .pio_base = (uint32_t *)PIO_BASE,
    .vtable.v_pio_odata_write = pio_odata_write,
    .vtable.v_pio_odata_read = pio_odata_read,
    .vtable.v_pio_oenable_write = pio_oenable_write,
    .vtable.v_pio_oenable_read = pio_oenable_read,
    .vtable.v_pio_idata_write = pio_idata_write,
    .vtable.v_pio_idata_read = pio_idata_read,
};

const struct metal_pio* pio_tables[] = {&metal_pio};
uint8_t pio_tables_cnt = 1;

const struct metal_pio* get_metal_pio(uint8_t idx)
{
    if (idx >= pio_tables_cnt)
        return NULL;
    return pio_tables[idx];
}
