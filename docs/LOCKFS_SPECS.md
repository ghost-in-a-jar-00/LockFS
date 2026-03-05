# Specifications of LockFS

This documentation will list the specifications of LockFS

## Contents

- [Fundamental Architecture](#Fundamental-Architecture)
- [File Format For LockFS](#File-Format-For-LockFS)
- [Current Algorithm Values](#Current-Algorithm-Values)
- [Byte Allocations For LockFS File Format](#Byte-Allocations-For-LockFS-File-Format)
  - [Header Parameters](#Header-Parameters)
  - [Length-Constrained Parameters](#Length-Constrained-Parameters)
  
## Fundamental Architecture

LockFS is designed around a minimal and focused architecture with the following principles:

- Perform a single, well-defined function per program (e.g. encryption, decryption)
- Avoid open-ended feature sets. Functionality is intentionally limited to keep tools predictable, user-friendly, and difficult to misuse
- Keep the codebase small and maintainable, removing obsolete or unnecessary features when better implementations become available
- Design the file format to be extensible and forward-compatible, allowing upgrades and parameter changes as long as the underlying cryptographic algorithms support them
- Minimize background operations (e.g. temporary file creation) to ensure no sensitive data is left behind

## File Format For LockFS

This is how a LockFS encrypted `.lkx` file would look like according to their versions

**LockFS1**

```
[Format Version]

[Encryption Algo]
[Encryption Mode]
[Encryption Padding]

[KDF Type]

[Salt Length]
[IV Length]
[Tag Length]

[Encryption Key Size]

[Chunk Size]

[KDF Memory Size]
[KDF Threads Usage]
[KDF Number of Iterations]

[Salt]

[IV for Path][Path Length][Path (ENCRYPTED)][Tag]

[IV for Data][Data Chunk (ENCRYPTED)][Tag]
```

**Note:** IV and Tag for data is written per chunk
**Note:** Some of these parameters may be omitted or customized for certain programs (e.g. vault metadata encryption/decryption which does not use chunk size to process encrypted data)

> The file format version will only change if there is a modification in the type of encryption algo and its necessary components like KDF algo for example

## Current Algorithm Values

- Cipher: AES-256-GCM
- KDF: Argon2id

- Parameters (Cipher):
  - Salt Length: 16 bytes
  - IV Length: 12 bytes
  - Key Size: 32 bytes (256 bits)
  - Tag Length: 16 bytes (128 bits)
  - Chunk Size: 128 KiB (131072 bytes)

- Parameters (KDF):
  - Memory: 64 MiB (65536 KiB)
  - Parallelism: 2
  - Iterations: 4

## Byte Allocations And Type For LockFS File Format

The following parameters are stored in the **LockFS file header** with fixed byte allocations

### Header Parameters

| Field | Size | Type | Description |
|------|------|------|-------------|
| Format Version | 8 bytes | UTF-8 string (null-padded) | Identifies the LockFS format version |
| Encryption Algorithm | 8 bytes | UTF-8 string (null-padded) | Encryption algorithm used (e.g. AES) |
| Encryption Mode | 4 bytes | UTF-8 string (null-padded) | Cipher mode of operation (e.g. GCM) |
| Encryption Padding | 10 bytes | UTF-8 string (null-padded) | Cipher padding scheme |
| KDF Type | 10 bytes | UTF-8 string (null-padded) | KDF used |
| Salt Length | 1 byte | `uint8` | Length of the salt |
| IV Length | 1 byte | `uint8` | Length of the IV |
| Tag Length | 1 byte | `uint8` | Length of the authentication tag |
| Encryption Key Size | 2 bytes | `uint16` (BE) | Size of the derived encryption key |
| Chunk Size | 4 bytes | `uint32` (BE) | Size of each encrypted data chunk |
| KDF Memory Size | 4 bytes | `uint32` (BE) | Memory parameter used by the KDF |
| KDF Threads Usage | 1 byte | `uint8` | Number of threads used by the KDF |
| KDF Number of Iterations | 1 byte | `uint8` | Number of KDF iterations |

### Length-Constrained Parameters

| Field | Size | Type | Description |
|------|------|------|-------------|
| Path Length | 2 bytes | `uint16` (BE) | Length of the encrypted path (maximum **65535 bytes**) |
