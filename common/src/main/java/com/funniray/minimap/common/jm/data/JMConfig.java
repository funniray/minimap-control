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
    public String allowRightClickTeleport =  "true";
    public String radarLateralDistance =  "512";
    public String radarVerticalDistance =  "320";
    public String maxAnimalsData =  "128";
    public String maxAmbientCreaturesData =  "128";
    public String maxMobsData =  "128";
    public String maxPlayersData =  "128";
    public String maxVillagersData =  "128";
    public String teleportEnabled = "false";
    public String crossDimTeleport = "true";
    public String renderRange = "0"; //Deprecated(?), but keeping for compatibility (maybe use min to calc)
    public String surfaceRenderRange = "0";
    public String caveRenderRange = "0";
    public String surfaceMapping = "ALL";
    public String topoMapping = "ALL";
    public String biomeMapping = "ALL";
    public String caveMapping = "ALL";
    public String radarEnabled = "ALL";
    public String playerRadarEnabled = "true";
    public String playerRadarNamesEnabled = "true";
    public String villagerRadarEnabled = "true";
    public String animalRadarEnabled = "true";
    public String mobRadarEnabled = "true";
    public String configVersion = new JMVersion().journeymap_version.full;

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
        clone.allowRightClickTeleport = this.allowRightClickTeleport;
        clone.radarLateralDistance = this.radarLateralDistance;
        clone.radarVerticalDistance = this.radarVerticalDistance;
        clone.maxAnimalsData = this.maxAnimalsData;
        clone.maxAmbientCreaturesData = this.maxAmbientCreaturesData;
        clone.maxMobsData = this.maxMobsData;
        clone.maxPlayersData = this.maxPlayersData;
        clone.maxVillagersData = this.maxVillagersData;
        clone.teleportEnabled = this.teleportEnabled;
        clone.crossDimTeleport = this.crossDimTeleport;
        clone.renderRange = this.renderRange;
        clone.surfaceRenderRange = this.surfaceRenderRange;
        clone.caveRenderRange = this.caveRenderRange;
        clone.surfaceMapping = this.surfaceMapping;
        clone.topoMapping = this.topoMapping;
        clone.biomeMapping = this.biomeMapping;
        clone.caveMapping = this.caveMapping;
        clone.radarEnabled = this.radarEnabled;
        clone.playerRadarEnabled = this.playerRadarEnabled;
        clone.playerRadarNamesEnabled = this.playerRadarNamesEnabled;
        clone.villagerRadarEnabled = this.villagerRadarEnabled;
        clone.animalRadarEnabled = this.animalRadarEnabled;
        clone.mobRadarEnabled = this.mobRadarEnabled;

        return clone;
    }
}
