package org.thing4.core.parser.thing.yaml.module;

import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;

class ExtraThingUID extends ThingUID {

  public ExtraThingUID(String id) {
    super(id);
  }

  public ThingTypeUID getThingType() {
    return new ThingTypeUID(getSegment(0), getSegment(1));
  }

}