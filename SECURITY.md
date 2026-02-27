# Security Policy for LockFS

## Currently Maintained Versions

**General Release**

`1.0.1`

**Development Release**

`1.0.2-dev.1`

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

- [u/Arcuru](https://www.reddit.com/user/Arcuru/)
  - Found on 2026 Feb 25
  - Logic bug preventing files beyond the first from encrypting
  - Affected versions: `<= 1.0.0`
  - Status: Patched as `1.0.1-dev.1`, Released as `1.0.1`
 
