package com.jeevansetu.algorithm;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyType;
import com.jeevansetu.model.ResourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequiredResourceEngine {

    public List<ResourceType> determineRequiredResources(Emergency emergency) {
        List<ResourceType> resources = new ArrayList<>();
        if (emergency == null || emergency.getType() == null) {
            resources.add(ResourceType.AMBULANCE);
            return resources;
        }

        EmergencyType type = emergency.getType();

        switch (type) {
            case ACCIDENT:
                resources.add(ResourceType.AMBULANCE);
                resources.add(ResourceType.POLICE);
                if (emergency.getInjuredCount() > 2 || emergency.isTrapped()) {
                    resources.add(ResourceType.HOSPITAL);
                }
                break;

            case FIRE:
                resources.add(ResourceType.FIRE_UNIT);
                resources.add(ResourceType.POLICE);
                if (emergency.getInjuredCount() > 0 || emergency.isTrapped() || emergency.isSevereBleeding()) {
                    resources.add(ResourceType.AMBULANCE);
                }
                break;

            case BUILDING_COLLAPSE:
                resources.add(ResourceType.FIRE_UNIT);
                resources.add(ResourceType.AMBULANCE);
                resources.add(ResourceType.POLICE);
                resources.add(ResourceType.HOSPITAL);
                resources.add(ResourceType.VOLUNTEER);
                break;

            case BLOOD_EMERGENCY:
                resources.add(ResourceType.HOSPITAL);
                resources.add(ResourceType.BLOOD_BANK);
                if (emergency.getInjuredCount() > 0) {
                    resources.add(ResourceType.AMBULANCE);
                }
                break;

            case MEDICAL:
                resources.add(ResourceType.AMBULANCE);
                resources.add(ResourceType.HOSPITAL);
                break;

            case POLICE:
                resources.add(ResourceType.POLICE);
                if (emergency.getInjuredCount() > 0 || emergency.isSevereBleeding()) {
                    resources.add(ResourceType.AMBULANCE);
                }
                break;

            case RESCUE:
                resources.add(ResourceType.FIRE_UNIT);
                resources.add(ResourceType.POLICE);
                resources.add(ResourceType.AMBULANCE);
                break;

            case FLOOD:
            case NATURAL_DISASTER:
                resources.add(ResourceType.FIRE_UNIT);
                resources.add(ResourceType.AMBULANCE);
                resources.add(ResourceType.POLICE);
                resources.add(ResourceType.SHELTER);
                resources.add(ResourceType.VOLUNTEER);
                break;

            default:
                resources.add(ResourceType.AMBULANCE);
                resources.add(ResourceType.POLICE);
                break;
        }

        return resources;
    }
}
