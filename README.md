# Example Skills for HABot

This demonstrates how to provide new skills for HABot by writing OSGi components implementing the Skill interface.

It also shows how to use the Eclipse SmartHome semantics to retrieve items.

Make sure HABot is installed and drop it into your openHAB addons folder.

It is meant to be used with the items from the demo package.

For example, say:

- _I'm cold_
- _It's cold in here_

It will try to identify an (unique) item which is a setpoint related to the Temperature property, and increase its value by 1.

However, if you include a location, like this:

- _It's too cold in the kitchen_
- _I'm in the living room and I'm cold_

it will try to identify an HVAC equipment in that location instead, and turn it on.

It will also use the CardBuilder to show the relevant card containing the item that has been acted upon.
