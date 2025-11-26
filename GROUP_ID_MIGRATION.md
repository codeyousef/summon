# Group ID Migration Guide

**Effective Date**: November 13, 2025 (Version 0.4.8.7)  
**Completion Date**: Version 0.5.2.7 (estimated Q1 2026)

---

## Overview

Summon is migrating from `io.github.codeyousef` to `codes.yousef` as the primary Maven group ID. This document explains
the migration timeline, steps, and what you need to do.

---

## Why Are We Migrating?

1. **Official Domain**: `codes.yousef` is our official domain
2. **Simpler Structure**: Shorter, cleaner group ID
3. **Best Practices**: Aligns with Maven Central conventions for domain-based group IDs
4. **Future-Proof**: Sets us up for long-term growth

---

## Migration Timeline

### Phase 1: Dual Publishing (Current - 0.4.8.7 through 0.5.0.0)

**Status**: ✅ Active  
**Duration**: ~2-3 months

- All releases published to **BOTH** group IDs:
    - `codes.yousef` (NEW - recommended)
    - `io.github.codeyousef` (LEGACY - deprecated)
- Users can choose either group ID
- No breaking changes
- Gradual migration encouraged

**Versions in this phase**:

- 0.4.8.7 ✅
- 0.4.9.0 ✅
- 0.4.9.1 ✅
- 0.5.2.7 ✅
- 0.5.2.7 ✅
- 0.5.0.0 ⚠️ FINAL version on old group ID

### Phase 2: New Group Only (0.5.2.7 onwards)

**Status**: 🔜 Upcoming  
**Start Date**: Q1 2026 (estimated)

- Releases published ONLY to `codes.yousef`
- Old group ID (`io.github.codeyousef`) no longer updated
- Users MUST migrate to continue receiving updates

---

## How to Migrate

### For Application Developers

#### Step 1: Update Dependencies

**Before (Old)**:

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.codeyousef:summon:0.4.7.0")
    implementation("io.github.codeyousef:summon-jvm:0.4.7.0")
}
```

**After (New)**:

```kotlin
// build.gradle.kts
dependencies {
    implementation("codes.yousef:summon:0.5.2.7")
    implementation("codes.yousef:summon-jvm:0.5.2.7")
}
```

#### Step 2: Clean Build

```bash
# Clean old artifacts
./gradlew clean

# Refresh dependencies
./gradlew --refresh-dependencies

# Build
./gradlew build
```

#### Step 3: Test

Run your tests to ensure everything works:

```bash
./gradlew test
```

**That's it!** No code changes required.

---

### For Library Authors

If you're building a library that depends on Summon:

#### Step 1: Update Your Dependencies

Update to `codes.yousef` in your `build.gradle.kts`:

```kotlin
dependencies {
    api("codes.yousef:summon:0.5.2.7")  // Use 'api' for libraries
}
```

#### Step 2: Communicate to Users

Add a note in your release notes:

```markdown
This version migrates to Summon's new group ID (codes.yousef).
No action required from users - dependencies are transitive.
```

#### Step 3: Test Thoroughly

Ensure transitive dependencies resolve correctly:

```bash
./gradlew dependencies --configuration runtimeClasspath
```

---

## What Changes?

### ✅ What STAYS the Same

- **Package names**: All remain `codes.yousef.*`
- **APIs**: No breaking changes
- **Functionality**: Identical behavior
- **Artifacts**: Same JAR contents
- **Versions**: Synchronized across both group IDs

### ⚠️ What CHANGES

- **Group ID**: `io.github.codeyousef` → `codes.yousef`
- **Maven coordinates**: Dependency declaration only
- **Publishing**: Eventual single location only

### ❌ What DOESN'T Change

- **Imports**: `import codes.yousef.summon.*` (unchanged)
- **Code**: No source code modifications needed
- **Configuration**: No config file changes

---

## Troubleshooting

### Issue: "Cannot resolve codes.yousef:summon:0.5.2.7"

**Solution**:

1. Ensure you're using Maven Central: `mavenCentral()` in repositories
2. Refresh dependencies: `./gradlew --refresh-dependencies`
3. Check your internet connection
4. Wait a few minutes (Maven Central sync delay)

### Issue: "Duplicate class found"

**Cause**: Both group IDs in your dependency tree

**Solution**:

```kotlin
configurations.all {
    resolutionStrategy {
        // Force new group ID
        force("codes.yousef:summon:0.5.2.7")

        // Exclude old group ID
        exclude(group = "io.github.codeyousef", module = "summon")
    }
}
```

### Issue: "Version conflict"

**Solution**:
Use consistent versions across all Summon dependencies:

```kotlin
val summonVersion = "0.5.2.7"

dependencies {
    implementation("codes.yousef:summon:$summonVersion")
    implementation("codes.yousef:summon-jvm:$summonVersion")
}
```

---

## For CI/CD Pipelines

### GitHub Actions

Update your workflow files:

```yaml
# Before
- name: Add dependencies
  run: |
    implementation("io.github.codeyousef:summon:0.4.7.0")

# After
- name: Add dependencies
  run: |
    implementation("codes.yousef:summon:0.5.2.7")
```

### Jenkins, GitLab CI, etc.

Update any hardcoded dependency strings in your pipeline scripts.

---

## Version Compatibility Matrix

| Summon Version | io.github.codeyousef | codes.yousef | Recommended               |
|----------------|----------------------|--------------|---------------------------|
| 0.4.7.0        | ✅                    | ❌            | Upgrade                   |
| 0.4.8.7        | ✅                    | ✅            | **Use codes.yousef**      |
| 0.4.9.0        | ✅                    | ✅            | **Use codes.yousef**      |
| 0.4.9.1        | ✅                    | ✅            | **Use codes.yousef**      |
| 0.5.2.7        | ✅                    | ✅            | **Use codes.yousef**      |
| 0.5.2.7        | ✅                    | ✅            | **Use codes.yousef**      |
| 0.5.0.0        | ✅ FINAL              | ✅            | **Use codes.yousef**      |
| 0.5.2.7+       | ❌                    | ✅            | **Must use codes.yousef** |

---

## FAQ

### Q: Do I need to change my code?

**A**: No. Only update the dependency declaration in `build.gradle.kts`. Your imports and code remain unchanged.

### Q: What if I don't migrate before 0.5.2.7?

**A**: You'll be stuck on version 0.5.0.0 or earlier. To get newer versions, you'll need to migrate.

### Q: Can I use both group IDs in the same project?

**A**: Not recommended. Pick one (preferably `codes.yousef`) to avoid duplicate classes.

### Q: Will old versions still work?

**A**: Yes. Versions published under `io.github.codeyousef` will remain available on Maven Central indefinitely.

### Q: How long do I have to migrate?

**A**: You have until we release 0.5.2.7 (estimated Q1 2026). However, we recommend migrating now to avoid issues later.

### Q: What happens to the old group ID after 0.5.0.0?

**A**: It remains on Maven Central for existing versions, but receives no new updates.

---

## Communication Channels

Stay informed about the migration:

- **GitHub Releases**: Check release notes for each version
- **README**: Updated with migration status
- **CHANGELOG**: Detailed version-specific changes
- **This Document**: Comprehensive migration guide

---

## Support

Need help with migration?

1. **Check this guide** for common issues
2. **Review README** for installation examples
3. **Search existing issues** on GitHub
4. **Open a new issue** with `migration` label

---

## Timeline Summary

```
November 2025: 0.4.8.7 released (dual publishing begins)
    ↓
December 2025: 0.4.9.0 (dual publishing continues)
    ↓
December 2025: 0.4.9.1 (dual publishing continues)
    ↓
January 2026: 0.5.0.0 (FINAL version on old group ID)
    ↓
February 2026: 0.5.2.7 (NEW group ID only)
```

---

## Checklist

Use this checklist to track your migration:

- [ ] Read this migration guide
- [ ] Update `build.gradle.kts` dependencies
- [ ] Clean build directory (`./gradlew clean`)
- [ ] Refresh dependencies (`./gradlew --refresh-dependencies`)
- [ ] Run tests (`./gradlew test`)
- [ ] Update CI/CD pipelines (if any)
- [ ] Update documentation (if applicable)
- [ ] Deploy and verify in staging
- [ ] Deploy to production
- [ ] Monitor for issues

---

## Thank You!

Thank you for using Summon and for your patience during this migration. We believe this change will benefit the project
and community in the long term.

**Questions?** Open an issue on GitHub or reach out to the maintainers.

---

**Last Updated**: November 13, 2025  
**Applies to**: Summon 0.5.2.7 and later

