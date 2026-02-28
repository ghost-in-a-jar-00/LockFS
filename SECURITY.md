# Security Policy for LockFS

## Currently Maintained Versions

**General Release**

`1.0.2`

**Development Release**

`2.0.0-dev.1`

## Reporting a Vulnerability

If you discover a security issue in LockFS, please report it by opening a [new security advisory](https://github.com/ghost-in-a-jar-00/LockFS/security/advisories)

This is the preferred and most secure way to ensure we can address the issue promptly

**Include the following details**

- Description of the security issue
- Steps to recreate it
- Version(s) affected
- Platform(s) affected

We will acknowledge receipt within **48 hours** and aim to provide a fix or guidance within **14 days**, depending on severity

## Patch and Release Policy

- Security fixes will be released promptly in the form of a **new version**
- Critical patches may be backported to previous versions at our discretion
- *Users are encouraged to update promptly to the latest secure version*

## Acknowledgements

- phnx (Privacy Guides)
  - Found on 2026 Feb 26
  - Pointed out modern day iteration for PBKDF2-HMAC-SHA256 uses 600k iterations instead of the current value of the project's implementation
  - Affected versions: `<= 1.0.1`
  - Status: Patched and released as `1.0.2`

- [u/Arcuru](https://www.reddit.com/user/Arcuru/)
  - Found on 2026 Feb 25
  - Logic bug preventing files beyond the first from encrypting
  - Affected versions: `<= 1.0.0`
  - Status: Patched as `1.0.1-dev.1`, released as `1.0.1`
 
