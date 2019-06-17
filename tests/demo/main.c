#include <stdio.h>
#include <stdlib.h>


#define PIO_BASE 0x60000

int main()
{
  // read/write to axi block
  volatile uint32_t * pio = (uint32_t *) PIO_BASE;

  volatile uint32_t * odata   = pio;
  volatile uint32_t * oenable = pio + 1;
  volatile uint32_t * idata   = pio + 2;

  uint32_t odatas[5] = {0xDEADBEEF, 0xF0F0F0F0, 0xABCD1234, 0x01234567, 0xFEDCBA98};
  uint32_t oenables[5] = {0xF0F0F0F0, 0x0F0F0F0F, 0xDEADBEEF, 0x89ABCDEF, 0x7654321};

  int fail = 0;
  for (int i = 0; i < 3; i++) {
    *odata = odatas[i];
    *oenable = oenables[i];

    fail |= ((*odata ^ *oenable) != *idata);
  }

  int test_len = 100;
  for (int i = 0; i < test_len; i++) {
    *odata = i;
    *oenable = test_len - i;

    fail |= ((*odata ^ *oenable) != *idata);
  }

  return fail;
}
