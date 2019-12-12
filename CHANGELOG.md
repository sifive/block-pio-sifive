# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Backwards-Incompatible Changes

- Upgrade to environment-blockci-sifive 0.3.0 and wake 0.17.1. See their
  respective changelogs for individual backwards-incompatible changes.
- Upgrade to latest api-generator-sifive and soc-testsocket-sifive. These
  packages do not yet have stable APIs.

## [0.1.0]

This represents the first versioned release of block-pio-sifive, and it contains a number of new features compared to previous "unversioned" commits.

### New Features

- Chisel elaboration now also emits an Object Model JSON and a Device Tree file with configuration-specific information about the PIO block.
- A set of C driver files for interfacing with the PIO block's registers, allowing software that uses the PIO block to be written in a more portable way. These drivers are also combined with a set of header files that contain configuration-specific information such as base address, which are generated automatically based on the design configuration.
- Wake functions for building a parameterized document that describes both the programming interface and RTL integration information.
- Continuous integration checks have been added, testing the above workflows on every pull request and commit to the repository.
- A second configuration of the PIO block with a width of 16 has been added. A second DUT has been created as well, with a Wake variable named `pio16DUT`. This allows for easily testing changes across multiple parameterizations of the PIO block.

### Backwards incompatible changes

- There are new dependencies or Ruby (~2.5) and Python (~3.7), so the running environment must have them installed.
- The directory structure has been changed for greater consistency between different file types.

[Unreleased]: https://github.com/olivierlacan/keep-a-changelog/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/olivierlacan/keep-a-changelog/compare/3ae174ec5bcae93674bc6ab16a9fa8177b41b9d7...v0.1.0
