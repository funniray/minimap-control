package com.funniray.minimap.common.version;

public class Version {
    private final int release;
    private final int major;
    private final int minor;

    public Version(int release, int major, int minor) {
        this.release = release;
        this.major = major;
        this.minor = minor;
    }

    public boolean greaterThanEqual(Version otherVersion) {
        if (this.release > otherVersion.release) return true;
        if (this.release < otherVersion.release) return false;
        if (this.major > otherVersion.major) return true;
        if (this.major < otherVersion.major) return false;
        if (this.minor > otherVersion.minor) return true;
        if (this.minor < otherVersion.minor) return false;
        return true;
    }

    public int getRelease() {
        return release;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String toString() {
        return String.format("%d.%d.%d",release,major,minor);
    }
}
