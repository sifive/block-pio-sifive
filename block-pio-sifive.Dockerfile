FROM sifive/environment-blockci:0.1.0

# Install npm
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash -
RUN apt-get update && apt-get install -y nodejs

# Install Python 3.7
RUN add-apt-repository ppa:deadsnakes/ppa && \
    apt-get update && \
    apt-get install -y python3.7 && \
    curl -L https://github.com/pypa/get-pip/raw/0eed4d8903dab820c92779716ab66c414d8b11a4/get-pip.py | python3.7 - 'pip==18.1' && \
  pip install pipenv==v2018.10.13

# Install RISC-V Toolchain
RUN curl -o /tmp/riscv-tools.tar.gz https://static.dev.sifive.com/e269a57e1153757314f52b1a5032cc387cf62010bc6bd6b742723dc431a48698/riscv64-unknown-elf-gcc-8.2.0-2019.05.3-x86_64-linux-ubuntu14.tar.gz && \
    tar xzf /tmp/riscv-tools.tar.gz -C /opt && \
    rm /tmp/riscv-tools.tar.gz

# Install Verilator
RUN curl -o /tmp/verilator.deb -L https://github.com/sifive/verilator/releases/download/4.016-0sifive1/verilator_4.016-0sifive1_amd64.deb && \
  apt install -y /tmp/verilator.deb && \
  rm /tmp/verilator.deb

# Install Python packages
RUN pip3 install pipenv==2018.11.26 pyfdt==0.3

COPY ./workspace /workspace

ENV PYTHONPATH=/workspace
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

WORKDIR /workspace
RUN wake --init .
RUN echo "[install] prefix=" > ~/.pydistutils.cfg && \
    npm i duh@1.13.1 && \
    export PATH=$PATH:./node_modules/.bin && \
    rm ~/.pydistutils.cfg && \
    duh-export-scala block-pio-sifive/loopback.json5 -o block-pio-sifive/craft/loopback/src && \
    duh-export-scala block-pio-sifive/pio.json5 -o block-pio-sifive/craft/pio/src

WORKDIR /workspace/block-pio-sifive
RUN git add craft

CMD bash
