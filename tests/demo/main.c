#include <stdlib.h>
#include <stdint.h>
#include "pio_drv_simple.h"
#include "metal_pio.h"

int main()
{
  // read/write to axi block
  uint32_t odatas[5] = {0xDEADBEEF, 0xF0F0F0F0, 0xABCD1234, 0x01234567, 0xFEDCBA98};
  uint32_t oenables[5] = {0xF0F0F0F0, 0x0F0F0F0F, 0xDEADBEEF, 0x89ABCDEF, 0x7654321};
  uint32_t read_value;
  const struct metal_pio * m_pio = &metal_pio;

  int fail = 0;
  for (int i = 0; i < 5; i++) {
    pio_write(odatas[i], oenables[i]);
    fail |= ((odatas[i] ^ oenables[i]) != pio_read());
  }

  int test_len = 100;
  for (int i = 0; i < test_len; i++) {
    pio_write(i, test_len - i );
    fail |= ((i ^ (test_len -i)) != pio_read());
  }

  for (int i = 0; i < 5; i++) {
    metal_pio_write(m_pio, odatas[i], oenables[i]);
    fail |= ((odatas[i] ^ oenables[i]) != metal_pio_read(m_pio));
  }

  for (int i = 0; i < test_len; i++) {
    metal_pio_write(m_pio, i, test_len - i );
    fail |= ((i ^ (test_len -i)) != metal_pio_read(m_pio));
  }

  return fail;
}
