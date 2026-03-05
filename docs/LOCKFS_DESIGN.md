# LockFS Design

This documentation explains how LockFS is designed

## Contents

- [Purpose of LockFS](Purpose-of-LockFS)
- [Target Audience](Target-Audience)
- [Minimum Specs](Minimum-Specs)
- [Content Privacy](Content-Privacy)
- [Language Selection](Language-Selection)
- [Dependency Usage](Dependency-Usage)
- [Algorithm Selection](Algorithm-Selection)
- [LockFS File Format Versioning](LockFS-File-Format-Versioning)
- [Known Limitations](Known-Limitations)

## Purpose of LockFS

LockFS is designed to be a file-by-file encryption tool that is minimal in program design, simple to implement and secure. It should use strong and up-to-date dependencies to achieve this. LockFS should be designed to be extensible by building related components by separating them into different programs to give everyone a choice to include certain features if it is needed, with the main focus on encryption and decryption

## Target Audience

LockFS is designed primarily for

- Privacy-conscious users
- Encrypted backups
- Backup strategies that involve storage media of varying sizes

Since LockFS is extensible, these should be used as a starting point and not a limiting factor when creating new tools

## Minimum Specs

LockFS values were chosen with the specifications of low end devices

- RAM: 4GB
- Threads: 4

If key derivation or encryption operations use too much memory or CPU, other processes may slow down or stall. Parameters are chosen to ensure strong cryptographic security while having minimal impact on device performance

## Content Privacy

LockFS is designed to avoid unintentional storage of plaintext data. This includes, but not limited to

- Temporary files
- Copied files for snapshots
- Original folders and files after encryption

Preventing leftover plaintexts reduces the risk of data leakage

## Language Selection

When choosing a language to build tools for LockFS, here are some guidelines to consider

- Easy to build on, maintain and read
- Memory safe with memory safety features
- Can be easily used and written on different setups with minimal dependency on platform-specific features (e.g. IDE, notepad with terminal, etc)
- Platform independent (The language should ideally cater to a wide range of OSes and CPU architectures without recompiling)
- Performance of tool (e.g. The language should not be a limiting factor when it comes to processing data due to how it is designed)

## Dependency Usage

All dependencies should be sourced from the standard libraries first. External dependencies should only be considered if the standard library is either insecure or lacks a required feature. This approach helps minimize potential security risks, reduces the overall attack surface and simplifies maintenance

## Algorithm Selection

These are some of the requirements to choose a suitable algorithm for LockFS

- **Maturity:** The algorithm should be well-studied, widely adopted, and vetted by the cryptography community
- **Resistance to known attacks:** It should resist brute-force, cryptanalysis, and side-channel attacks
- **Interoperability:** It should work across different systems, libraries, and platforms
- **Performance:** It should be efficient enough for the expected workload without compromising security
- **Configurability:** Ability to adjust parameters like key size, memory, or iteration counts
- **Future-proof:** Resistance to foreseeable threats and ability to upgrade if needed

## LockFS File Format Versioning

LockFS file formats are designed to change only when the type of encryption algorithm or its essential components are modified. This ensures that files encrypted with the same algorithm remain compatible across versions, even if parameters such as key size or chunk size differ. For example, a file encrypted with a 512 bit key should still be processed correctly by a program using the same algorithm with a 256 bit key

## Known Limitations

Some of the weaknesses of LockFS are known during the planning phase

- Unencrypted texts and folders get deleted, not wiped
  - This choice was made to balance speed and security
  - Unlikely to be recovered under normal use
  - If this is a concern, periodic disk wiping should be done with preventive measures like enabling disk encryption and `fstrim` or its equivalent
- Certain protected areas of the OS can still contain unencrypted data and metadata related to the file containing the unencrypted data
  - **Note:** This limitation is due to system-level behavior and are not faults of LockFS itself
  - Swaps and Pagefiles for example can still contain unencrypted data left during processing
  - Reducing chunk size slightly limits the window that unencrypted data is in memory, but cannot guarantee that data will not be paged out
  - Under normal use, this data is unlikely to be accessed by an attacker
  - If this is a concern, periodic disk wiping should be done with preventive measures like enabling disk encryption
- Artefacts can still remain in RAM after process is complete
  - **Note:** This is an inherent limitation of how operating systems manage memory
  - Chunks of data may still remain here
  - Under normal use, this should be a negligible trade-off for typical usage
  - Rebooting of the device is recommended to remove remaining data from RAM
