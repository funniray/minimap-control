package com.funniray.minimap.common.jm.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class JMConfig {
    public String journeymapEnabled = "true";
    public String useWorldId = "true";
    public String viewOnlyServerProperties = "true";
    public String allowMultiplayerSettings = "ALL";
    public String worldPlayerRadar = "ALL";
    public String worldPlayerRadarUpdateTime = "5";
    public String seeUndergroundPlayers = "ALL";
    public String hideOps = "false";
    public String hideSpectators = "false";
    public String allowDeathPoints = "true";
    public String showInGameBeacons = "true";
    public String allowWaypoints = "true";
    public String teleportEnabled = "false";
    public String renderRange = "0";
    public String surfaceMapping = "ALL";
    public String topoMapping = "ALL";
    public String biomeMapping = "ALL";
    public String caveMapping = "ALL";
    public String radarEnabled = "ALL";
    public String playerRadarEnabled = "true";
    public String villagerRadarEnabled = "true";
    public String animalRadarEnabled = "true";
    public String mobRadarEnabled = "true";
    public String configVersion = JMVersion.journeymap_version.full;

    public JMConfig copy() {
        JMConfig clone = new JMConfig();
        clone.journeymapEnabled = this.journeymapEnabled;
        clone.useWorldId = this.useWorldId;
        clone.viewOnlyServerProperties = this.viewOnlyServerProperties;
        clone.allowMultiplayerSettings = this.allowMultiplayerSettings;
        clone.worldPlayerRadar = this.worldPlayerRadar;
        clone.worldPlayerRadarUpdateTime = this.worldPlayerRadarUpdateTime;
        clone.seeUndergroundPlayers = this.seeUndergroundPlayers;
        clone.hideOps = this.hideOps;
        clone.hideSpectators = this.hideSpectators;
        clone.allowDeathPoints = this.allowDeathPoints;
        clone.showInGameBeacons = this.showInGameBeacons;
        clone.allowWaypoints = this.allowWaypoints;
        clone.teleportEnabled = this.teleportEnabled;
        clone.renderRange = this.renderRange;
        clone.surfaceMapping = this.surfaceMapping;
        clone.topoMapping = this.topoMapping;
        clone.biomeMapping = this.biomeMapping;
        clone.caveMapping = this.caveMapping;
        clone.radarEnabled = this.radarEnabled;
        clone.playerRadarEnabled = this.playerRadarEnabled;
        clone.villagerRadarEnabled = this.villagerRadarEnabled;
        clone.animalRadarEnabled = this.animalRadarEnabled;
        clone.mobRadarEnabled = this.mobRadarEnabled;

        return clone;
    }
}
